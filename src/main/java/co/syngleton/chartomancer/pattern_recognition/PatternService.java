package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.core_entities.*;
import co.syngleton.chartomancer.shared_constants.CoreDataSettingNames;
import co.syngleton.chartomancer.util.Calc;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import co.syngleton.chartomancer.util.Futures;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
@AllArgsConstructor
final class PatternService implements PatternGenerator, PatternComputer {
    private final PatternFactory patternFactory;
    private final CandleRescaler candleRescaler;
    private final Analyzer analyzer;

    @Override
    public List<Pattern> createPatterns(PatternSettings.Builder settingsInput) {
        return patternFactory.create(settingsInput);
    }

    @Override
    public boolean computeCoreData(CoreData coreData, ComputationSettings.Builder settingsInput) {

        if (isBroken(coreData)) {
            return false;
        }

        boolean successfulComplete = true;

        for (Graph graph : coreData.getReadOnlyGraphs()) {

            List<Pattern> patternsToCompute = coreData.getPatterns(graph.getSymbol(), graph.getTimeframe());

            if (Check.isEmpty(patternsToCompute)) {
                successfulComplete = false;
                continue;
            }

            List<Pattern> computedPatterns = computePatterns(
                    settingsInput
                            .patterns(patternsToCompute)
                            .graph(graph)
            );

            if (Check.isEmpty(computedPatterns)) {
                successfulComplete = false;
            }

            coreData.putPatterns(computedPatterns, graph.getSymbol(), graph.getTimeframe());
            updateCoreDataComputationSettings(coreData, settingsInput.build());

        }
        return successfulComplete;
    }

    private boolean isBroken(CoreData coreData) {
        if (coreData == null || coreData.hasInvalidStructure()) {
            log.error("! Core data instance is null or broken !");
            return true;
        }
        return false;
    }

    @Override
    public List<Pattern> computePatterns(ComputationSettings.Builder settingsInput) {

        ComputationSettings computationSettings = settingsInput.build();

        if (Objects.requireNonNull(computationSettings.getComputationType()) == ComputationType.BASIC_ITERATION) {
            try {
                return computeBasicIterationPatterns(computationSettings);
            } catch (ExecutionException e) {
                e.printStackTrace();
                return Collections.emptyList();
            } catch (InterruptedException e) {
                log.error("Interrupted!", e);
                Thread.currentThread().interrupt();
                return Collections.emptyList();
            }
        }
        log.error("Undefined pattern type.");
        return Collections.emptyList();
    }

    private List<Pattern> computeBasicIterationPatterns(ComputationSettings computationSettings) throws ExecutionException, InterruptedException {

        if (!areValidComputablePatterns(computationSettings.getPatterns())) {
            log.warn("No computable patterns found for settings: " + computationSettings);
            return Collections.emptyList();
        }

        List<Pattern> computedPatterns;
        ProgressBar pb = startProgressBar(computationSettings);
        computedPatterns = launchMultiThreadedComputations(computationSettings, pb);
        stopProgressBar(pb);

        return filterOutUselessPatterns(computedPatterns);
    }

    private boolean areValidComputablePatterns(List<Pattern> patterns) {
        log.debug(patterns.get(0).getClass().toString());
        return Check.isNotEmpty(patterns) && patterns.get(0) instanceof ComputablePattern;
    }

    private ProgressBar startProgressBar(ComputationSettings computationSettings) {
        ProgressBar pb = new ProgressBar("Processing " + computationSettings.getGraph().getSymbol() + ", " + computationSettings.getGraph().getTimeframe(), computationSettings.getPatterns().size());
        return pb.start();
    }

    private synchronized void incrementProgressBar(ProgressBar pb) {
        pb.step();
    }

    private void stopProgressBar(ProgressBar pb) {
        pb.stop();
    }

    private List<Pattern> launchMultiThreadedComputations(ComputationSettings computationSettings, ProgressBar pb) throws ExecutionException, InterruptedException {
        return Futures.listCompleted(
                Collections.unmodifiableList(computationSettings.getPatterns())
                        .stream()
                        .map(pattern -> CompletableFuture.supplyAsync(() -> computeBasicIterationPattern(pattern, computationSettings, pb)))
                        .toList()
        );
    }

    private Pattern computeBasicIterationPattern(Pattern pattern, ComputationSettings computationSettings, ProgressBar pb) {

        if (pattern instanceof MultiComputablePattern multiComputablePattern) {
            return computeBasicIterationPattern(multiComputablePattern, computationSettings, pb);
        }
        if (pattern instanceof ComputablePattern computablePattern) {
            return computeBasicIterationPattern(computablePattern, computationSettings, pb);
        }
        throw new IllegalArgumentException("Supplied pattern is not computable.");
    }

    Pattern computeBasicIterationPattern(ComputablePattern pattern, ComputationSettings computationSettings, ProgressBar pb) {

        int matchScore;
        float priceVariation;

        int computations = computationSettings.getGraph().getFloatCandles().size() - pattern.getLength() - pattern.getScope() + 1;

        float divider = 1;

        for (var i = 0; i < computations; i++) {

            priceVariation = analyzer.calculatePriceVariation(getFollowingFloatCandles(pattern, computationSettings, i), pattern.getScope());
            priceVariation = analyzer.filterPriceVariation(priceVariation);

            if (priceVariation != 0) {

                List<IntCandle> intCandlesToMatch = candleRescaler.rescale(getCandlesToMatch(pattern, computationSettings, i), pattern.getGranularity());
                matchScore = analyzer.calculateMatchScore(pattern.getIntCandles(), intCandlesToMatch);
                pattern.setPriceVariationPrediction(pattern.getPriceVariationPrediction() + Calc.xPercentOfY(matchScore, priceVariation));
                divider = incrementDivider(divider, matchScore);
            }
        }
        adjustPriceVariationPrediction(pattern, divider);
        incrementProgressBar(pb);

        return pattern;
    }

    private Pattern computeBasicIterationPattern(MultiComputablePattern pattern, ComputationSettings computationSettings, ProgressBar pb) {

        int computations = computationSettings.getGraph().getFloatCandles().size() - pattern.getLength() - pattern.getScope() + 1;

        float[] dividers = new float[pattern.getScope()];
        Arrays.fill(dividers, 1);

        for (var i = 0; i < computations; i++) {

            List<IntCandle> intCandlesToMatch = candleRescaler.rescale(getCandlesToMatch(pattern, computationSettings, i), pattern.getGranularity());
            int matchScore = analyzer.calculateMatchScore(pattern.getIntCandles(), intCandlesToMatch);
            List<FloatCandle> followingCandles = getFollowingFloatCandles(pattern, computationSettings, i);

            for (var candleIndex = 1; candleIndex <= pattern.getScope(); candleIndex++) {

                float priceVariation = analyzer.calculatePriceVariation(followingCandles, candleIndex);
                priceVariation = analyzer.filterPriceVariation(priceVariation);

                if (priceVariation != 0) {
                    pattern.setPriceVariationPrediction(pattern.getPriceVariationPrediction() + Calc.xPercentOfY(matchScore, priceVariation));
                    dividers[candleIndex - 1] = incrementDivider(dividers[candleIndex - 1], matchScore);
                }
            }
        }
        adjustPriceVariationPrediction(pattern, dividers);
        incrementProgressBar(pb);

        return pattern;
    }

    private List<FloatCandle> getFollowingFloatCandles(ComputablePattern pattern, ComputationSettings computationSettings, int pivot) {
        return computationSettings.getGraph().getFloatCandles().subList(pivot + pattern.getLength(), pivot + pattern.getLength() + pattern.getScope());
    }

    private List<FloatCandle> getCandlesToMatch(ComputablePattern pattern, ComputationSettings computationSettings, int pivot) {
        return computationSettings.getGraph().getFloatCandles().subList(pivot, pivot + pattern.getLength());
    }

    private float incrementDivider(float divider, int matchScore) {
        return divider + matchScore / 100f;
    }

    private void adjustPriceVariationPrediction(ComputablePattern pattern, float divider) {
        pattern.setPriceVariationPrediction(pattern.getPriceVariationPrediction() / divider);
    }

    private void adjustPriceVariationPrediction(MultiComputablePattern pattern, float[] dividers) {
        for (int i = 1; i <= pattern.getScope(); i++) {
            pattern.setPriceVariationPrediction(pattern.getPriceVariationPrediction(i) / dividers[i - 1], i);
        }
    }

    private List<Pattern> filterOutUselessPatterns(List<Pattern> patterns) {
        return patterns.stream().filter(pattern -> ((ComputablePattern) pattern).getPriceVariationPrediction() != 0).toList();
    }

    private void updateCoreDataComputationSettings(@NonNull CoreData coreData, @NonNull ComputationSettings computationSettings) {
        coreData.setPatternSetting(CoreDataSettingNames.COMPUTATION_TYPE, computationSettings.getComputationType().toString());
        coreData.setPatternSetting(CoreDataSettingNames.COMPUTATION_AUTOCONFIG, computationSettings.getAutoconfig().toString());
        coreData.setPatternSetting(CoreDataSettingNames.EXTRAPOLATE_MATCH_SCORE, Boolean.toString(analyzer.extrapolateMatchScore()));
        coreData.setPatternSetting(CoreDataSettingNames.EXTRAPOLATE_PRICE_VARIATION, Boolean.toString(analyzer.extrapolatePriceVariation()));
        coreData.setPatternSetting(CoreDataSettingNames.MATCH_SCORE_THRESHOLD, Double.toString(analyzer.matchScoreThreshold()));
        coreData.setPatternSetting(CoreDataSettingNames.PRICE_VARIATION_THRESHOLD, Double.toString(analyzer.priceVariationThreshold()));
        coreData.setPatternSetting(CoreDataSettingNames.MATCH_SCORE_SMOOTHING, analyzer.matchScoreSmoothing().toString());
        coreData.setPatternSetting(CoreDataSettingNames.COMPUTATION_DATE, Format.toFileNameCompatibleDateTime(LocalDateTime.now()));
    }
}

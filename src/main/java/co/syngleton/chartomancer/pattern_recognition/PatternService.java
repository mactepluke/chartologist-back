package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.core_entities.*;
import co.syngleton.chartomancer.shared_constants.CoreDataSettingNames;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import co.syngleton.chartomancer.util.Futures;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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

        boolean result = false;

        Set<PatternBox> computedPatternBoxes = new HashSet<>();

        if (coreData != null
                && Check.isNotEmpty(coreData.getGraphs())
                && Check.isNotEmpty(coreData.getPatternBoxes())
        ) {
            for (PatternBox patternBox : coreData.getPatternBoxes()) {

                if ((patternBox != null) && Check.isNotEmpty(patternBox.getPatterns())) {

                    Graph matchingGraph = patternBox.getFirstMatchingChartObjectIn(coreData.getGraphs());

                    if (matchingGraph != null) {

                        List<Pattern> computedPatterns = computePatterns(
                                settingsInput
                                        .patterns(coreData.getPatterns(matchingGraph.getSymbol(), matchingGraph.getTimeframe()))
                                        .graph(matchingGraph)
                        );

                        if (Check.isNotEmpty(computedPatterns)) {
                            computedPatternBoxes.add(
                                    new PatternBox(
                                            matchingGraph,
                                            computedPatterns
                                    )
                            );
                        }
                    }
                }
            }
            if (Check.isNotEmpty(computedPatternBoxes)) {
                coreData.setPatternBoxes(computedPatternBoxes);
                updateCoreDataComputationSettings(coreData, settingsInput.build());
            }
            result = true;
        }
        return result;
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

        List<Pattern> computedPatterns = new ArrayList<>();

        if (areValid(computationSettings.getPatterns())) {

            ProgressBar pb = startProgressBar(computationSettings);
            computedPatterns = launchMultiThreadedComputations(computationSettings, pb);
            stopProgressBar(pb);

        } else {
            log.error("Could not compute: no computable patterns found.");
        }
        return filterOutUselessPatterns(computedPatterns);
    }

    private boolean areValid(List<Pattern> patterns) {
        return (!patterns.isEmpty() && patterns.get(0) instanceof ComputablePattern);
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
        return Futures.listCompleted(Collections.unmodifiableList(computationSettings.getPatterns()).stream().map(pattern -> CompletableFuture.supplyAsync(() -> computeBasicIterationPattern((ComputablePattern) pattern, computationSettings, pb))).toList());
    }

    private Pattern computeBasicIterationPattern(ComputablePattern pattern, ComputationSettings computationSettings, ProgressBar pb) {

        int matchScore;
        float priceVariation;

        if (pattern != null) {

            int computations = computationSettings.getGraph().getFloatCandles().size() - pattern.getLength() - pattern.getScope() + 1;

            float divider = 1;

            for (var i = 0; i < computations; i++) {

                priceVariation = analyzer.calculatePriceVariation(getFollowingFloatCandles(pattern, computationSettings, i), pattern.getScope());
                priceVariation = analyzer.filterPriceVariation(priceVariation);

                if (priceVariation != 0) {

                    List<IntCandle> intCandlesToMatch = candleRescaler.rescale(getCandlesToMatch(pattern, computationSettings, i), pattern.getGranularity());
                    matchScore = analyzer.calculateMatchScore(pattern.getIntCandles(), intCandlesToMatch);
                    pattern.setPriceVariationPrediction(pattern.getPriceVariationPrediction() + priceVariation * (matchScore / 100f));
                    divider = incrementDivider(divider, matchScore);
                }
            }
            adjustPriceVariationPrediction(pattern, divider);
        }
        incrementProgressBar(pb);
        return (Pattern) pattern;
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

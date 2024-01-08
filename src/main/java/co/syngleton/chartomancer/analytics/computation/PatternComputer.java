package co.syngleton.chartomancer.analytics.computation;

import co.syngleton.chartomancer.charting.CandleNormalizer;
import co.syngleton.chartomancer.domain.ComputablePattern;
import co.syngleton.chartomancer.domain.FloatCandle;
import co.syngleton.chartomancer.domain.IntCandle;
import co.syngleton.chartomancer.domain.Pattern;
import co.syngleton.chartomancer.global.tools.Futures;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
@Component
public class PatternComputer {
    //TODO Fusionner avec PatternComputingService ?
    private final CandleNormalizer candleNormalizer;
    @Getter
    @Setter
    private Analyzer analyzer;
    private ComputationSettings computationSettings;
    private ProgressBar pb;

    @Autowired
    public PatternComputer(CandleNormalizer candleNormalizer, Analyzer analyzer) {
        this.candleNormalizer = candleNormalizer;
        this.analyzer = analyzer;
    }

    public List<Pattern> compute(ComputationSettings.Builder paramsInput) {

        computationSettings = paramsInput.build();

        if (Objects.requireNonNull(computationSettings.getComputationType()) == ComputationType.BASIC_ITERATION) {
            try {
                return computeBasicIterationPatterns();
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

    private List<Pattern> computeBasicIterationPatterns() throws ExecutionException, InterruptedException {

        List<Pattern> computedPatterns = new ArrayList<>();

        if (areValid(computationSettings.getPatterns())) {

            startProgressBar();
            computedPatterns = launchMultiThreadedComputations();
            stopProgressBar();

        } else {
            log.error("Could not compute: no computable patterns found.");
        }
        return filterOutUselessPatterns(computedPatterns);
    }

    private boolean areValid(List<Pattern> patterns) {
        return (!patterns.isEmpty() && patterns.get(0) instanceof ComputablePattern);
    }

    private void startProgressBar() {
        pb = new ProgressBar("Processing " + computationSettings.getGraph().getSymbol() + ", " + computationSettings.getGraph().getTimeframe(), computationSettings.getPatterns().size());
        pb.start();
    }

    private void incrementProgressBar() {
        pb.step();
    }

    private void stopProgressBar() {
        pb.stop();
    }

    private List<Pattern> launchMultiThreadedComputations() throws ExecutionException, InterruptedException {

        return Futures.listCompleted(Collections.unmodifiableList(computationSettings.getPatterns()).stream().map(pattern -> CompletableFuture.supplyAsync(() -> computeBasicIterationPattern((ComputablePattern) pattern))).toList());
    }

    private Pattern computeBasicIterationPattern(ComputablePattern pattern) {

        int matchScore;
        float priceVariation;

        if (pattern != null) {

            int computations = computationSettings.getGraph().getFloatCandles().size() - pattern.getLength() - pattern.getScope() + 1;

            float divider = 1;

            for (var i = 0; i < computations; i++) {

                priceVariation = analyzer.calculatePriceVariation(getFollowingFloatCandles(pattern, i), pattern.getScope());
                priceVariation = analyzer.filterPricePrediction(priceVariation);

                if (priceVariation != 0) {

                    List<IntCandle> intCandlesToMatch = candleNormalizer.normalizeCandles(getCandlesToMatch(pattern, i), pattern.getGranularity());
                    matchScore = analyzer.calculateMatchScore(pattern.getIntCandles(), intCandlesToMatch);
                    pattern.setPriceVariationPrediction(pattern.getPriceVariationPrediction() + priceVariation * (matchScore / 100f));
                    divider = incrementDivider(divider, matchScore);
                }
            }
            adjustPriceVariationPrediction(pattern, divider);
        }
        incrementProgressBar();
        return (Pattern) pattern;
    }

    private List<FloatCandle> getFollowingFloatCandles(ComputablePattern pattern, int pivot) {
        return computationSettings.getGraph().getFloatCandles().subList(pivot + pattern.getLength(), pivot + pattern.getLength() + pattern.getScope());
    }

    private List<FloatCandle> getCandlesToMatch(ComputablePattern pattern, int pivot) {
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

}



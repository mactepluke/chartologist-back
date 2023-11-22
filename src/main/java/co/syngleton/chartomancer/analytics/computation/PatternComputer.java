package co.syngleton.chartomancer.analytics.computation;

import co.syngleton.chartomancer.analytics.factory.CandleFactory;
import co.syngleton.chartomancer.analytics.model.ComputablePattern;
import co.syngleton.chartomancer.analytics.model.FloatCandle;
import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.IntCandle;
import co.syngleton.chartomancer.analytics.model.Pattern;
import co.syngleton.chartomancer.analytics.model.PatternType;
import co.syngleton.chartomancer.analytics.model.PredictivePattern;
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

    private final CandleFactory candleFactory;
    @Getter
    @Setter
    private Analyzer analyzer;

    @Autowired
    public PatternComputer(CandleFactory candleFactory, Analyzer analyzer) {
        this.candleFactory = candleFactory;
        this.analyzer = analyzer;
    }

    public String printAnalyserConfig() {
        return analyzer.toString();
    }

    public List<Pattern> compute(ComputationSettings.Builder paramsInput) {

        initializeCheckVariables();
        ComputationSettings computationSettings = configParams(paramsInput);

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

    private void initializeCheckVariables() {
        //TODO define, initialize computation settings and implement their use in corresponding methods
    }

    private ComputationSettings configParams(ComputationSettings.Builder paramsInput) {
//TODO refine this method
        return paramsInput.build();
    }

    private List<Pattern> computeBasicIterationPatterns(ComputationSettings computationSettings) throws ExecutionException, InterruptedException {

        List<Pattern> patterns = Collections.unmodifiableList(computationSettings.getPatterns());
        Graph graph = computationSettings.getGraph();
        List<Pattern> computedPatterns = new ArrayList<>();

        String pbMessage = "Processing " + graph.getSymbol() + ", " + graph.getTimeframe();

        if (areComputable(patterns)) {

            ProgressBar pb = new ProgressBar(pbMessage, patterns.size());
            pb.start();

            computedPatterns = Futures.listCompleted(
                            patterns.stream().map(pattern -> CompletableFuture.supplyAsync(
                                            () -> computeBasicIterationPattern((ComputablePattern) pattern, graph, pb)
                                    ))
                                    .toList()
                    )
                    .stream()
                    .filter(pattern -> ((ComputablePattern) pattern).getPriceVariationPrediction() != 0)
                    .toList();

            pb.stop();
        } else {
            log.error("Could not compute: no computable patterns found.");
        }
        if (computedPatterns.isEmpty()) {
            computedPatterns = null;
        }
        return computedPatterns;
    }

    private boolean areComputable(List<Pattern> patterns) {
        return (!patterns.isEmpty()
                && (patterns.get(0).getPatternType() == PatternType.PREDICTIVE));
    }

    private Pattern computeBasicIterationPattern(ComputablePattern computablePattern, Graph graph, ProgressBar pb) {

        int matchScore = 0;
        float priceVariation;

        if (computablePattern != null) {

            int computations = graph.getFloatCandles().size() - computablePattern.getLength() - computablePattern.getScope() + 1;

            float divider = 1;

            for (var i = 0; i < computations; i++) {

                List<FloatCandle> candlesToMatches = graph.getFloatCandles().subList(i, i + computablePattern.getLength());
                List<FloatCandle> followingFloatCandles = graph.getFloatCandles().subList(i + computablePattern.getLength(), i + computablePattern.getLength() + computablePattern.getScope());

                priceVariation = analyzer.calculatePriceVariation(followingFloatCandles, computablePattern.getScope());

                if (priceVariation != 0) {

                    if (Objects.requireNonNull(computablePattern.getPatternType()) == PatternType.PREDICTIVE) {
                        List<IntCandle> intCandlesToMatch = candleFactory.streamlineToIntCandles(candlesToMatches, computablePattern.getGranularity());
                        assert computablePattern instanceof PredictivePattern;
                        matchScore = analyzer.calculateMatchScore((PredictivePattern) computablePattern, intCandlesToMatch);
                    } else {
                        log.error("Could not compute patterns because of unrecognized pattern type: {}", computablePattern.getPatternType());
                    }

                    computablePattern.setPriceVariationPrediction(
                            computablePattern.getPriceVariationPrediction() + priceVariation * (matchScore / 100f)
                    );
                    divider = divider + matchScore / 100f;
                }
            }

            computablePattern.setPriceVariationPrediction(computablePattern.getPriceVariationPrediction() / divider);

        }
        pb.step();
        return (Pattern) computablePattern;
    }


}



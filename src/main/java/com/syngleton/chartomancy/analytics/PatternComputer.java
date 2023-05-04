package com.syngleton.chartomancy.analytics;

import com.syngleton.chartomancy.factory.CandleFactory;
import com.syngleton.chartomancy.model.charting.candles.FloatCandle;
import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.patterns.*;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
@Component
public class PatternComputer {

    private final CandleFactory candleFactory;
    private final Analyzer analyzer;

    @Autowired
    public PatternComputer(CandleFactory candleFactory, Analyzer analyzer) {
        this.candleFactory = candleFactory;
        this.analyzer = analyzer;
    }

    public List<Pattern> compute(ComputationSettings.Builder paramsInput) {

        initializeCheckVariables();
        ComputationSettings computationSettings = configParams(paramsInput);

        switch (computationSettings.getComputationType()) {
            case BASIC_ITERATION -> {
                return computeBasicIterationPatterns(computationSettings);
            }
            default -> {
                log.error("Undefined pattern type.");
                return Collections.emptyList();
            }
        }
    }

    private ComputationSettings configParams(ComputationSettings.Builder paramsInput) {
//TODO refine this method
        return paramsInput.build();
    }

    private void initializeCheckVariables() {
        //TODO define, initialize computation settings and implement their use in corresponding methods
    }

    private List<Pattern> computeBasicIterationPatterns(ComputationSettings computationSettings) {

        List<Pattern> patterns = computationSettings.getPatterns();
        Graph graph = computationSettings.getGraph();
        List<Pattern> computedPatterns = new ArrayList<>();

        String pbMessage = "Processing " + graph.getSymbol() + ", " + graph.getTimeframe();

        if (!patterns.isEmpty()
                && (patterns.get(0).getPatternType() == PatternType.PREDICTIVE
                || patterns.get(0).getPatternType() == PatternType.LIGHT_PREDICTIVE)) {

            ProgressBar pb = new ProgressBar(pbMessage, patterns.size());

            pb.start();
            for (Pattern pattern : patterns) {
                pb.step();
                computedPatterns.add(computeBasicIterationPattern((ComputablePattern) pattern, graph));
            }
            pb.stop();
        } else {
            log.error("Could not compute: no computable patterns found.");
        }
        return computedPatterns;
    }

    private Pattern computeBasicIterationPattern(ComputablePattern computablePattern, Graph graph) {

        int matchScore = 0;
        float priceVariation = 0;

        if (computablePattern != null) {

            float startPricePrediction = computablePattern.getPriceVariationPrediction();
            LocalDateTime startTime = LocalDateTime.now();

            int computations = graph.getFloatCandles().size() - computablePattern.getLength() - computablePattern.getScope() + 1;

            float divider = 1;

            for (var i = 0; i < computations; i++) {

                List<FloatCandle> candlesToMatches = graph.getFloatCandles().subList(i, i + computablePattern.getLength());
                List<FloatCandle> followingFloatCandles = graph.getFloatCandles().subList(i + computablePattern.getLength(), i + computablePattern.getLength() + computablePattern.getScope());

                priceVariation = analyzer.calculatePriceVariation(followingFloatCandles, computablePattern.getScope());

                switch (computablePattern.getPatternType()) {
                    case LIGHT_PREDICTIVE -> {
                        List<IntCandle> intCandlesToMatch = candleFactory.streamlineToIntCandles(candlesToMatches, computablePattern.getGranularity());
                        assert computablePattern instanceof LightPredictivePattern;
                        matchScore = analyzer.calculateMatchScoreWithExponentialSmoothing((LightPredictivePattern) computablePattern, intCandlesToMatch);
                    }
                    case PREDICTIVE -> {
                        List<PixelatedCandle> pixelatedCandlesToMatch = candleFactory.pixelateCandles(candlesToMatches, computablePattern.getGranularity());
                        assert computablePattern instanceof PredictivePattern;
                        matchScore = analyzer.calculateMatchScore((PredictivePattern) computablePattern, pixelatedCandlesToMatch);
                    }
                    default ->
                            log.error("Could not compute patterns because of unrecognized pattern type: {}", computablePattern.getPatternType());
                }

                computablePattern.setPriceVariationPrediction(
                        computablePattern.getPriceVariationPrediction() + priceVariation * (matchScore / 100f)
                );
                divider = divider + matchScore / 100f;
            }

            computablePattern.setPriceVariationPrediction(computablePattern.getPriceVariationPrediction() / divider);

            computablePattern.getComputationsHistory().add(new ComputationData(
                    startTime,
                    LocalDateTime.now(),
                    ComputationType.BASIC_ITERATION,
                    computations,
                    startPricePrediction,
                    computablePattern.getPriceVariationPrediction()
            ));
        }
        return (Pattern) computablePattern;
    }


}



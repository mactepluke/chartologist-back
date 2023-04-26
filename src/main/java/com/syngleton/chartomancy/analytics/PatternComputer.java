package com.syngleton.chartomancy.analytics;

import com.syngleton.chartomancy.factory.CandleFactory;
import com.syngleton.chartomancy.model.charting.*;
import com.syngleton.chartomancy.util.Format;
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

    @Autowired
    public PatternComputer(CandleFactory candleFactory) {
        this.candleFactory = candleFactory;
    }

    public List<Pattern> compute(ComputationSettings.Builder paramsInput)   {

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

    public List<Pattern> computeBasicIterationPatterns(ComputationSettings computationSettings) {

        List<Pattern> patterns = computationSettings.getPatterns();
        Graph graph = computationSettings.getGraph();
        List<Pattern> computedPatterns = new ArrayList<>();

        log.debug("Computing basic iteration patterns for graph of symbol: {}, timeframe: {}", graph.getSymbol(), graph.getTimeframe());

        if (!patterns.isEmpty() && patterns.get(0).getPatternType() == PatternType.PREDICTIVE) {

            ProgressBar pb = new ProgressBar("Processing...", patterns.size());

            pb.start();
            for (Pattern pattern : patterns) {
                pb.step();
                computedPatterns.add(computeBasicIterationPattern((PredictivePattern) pattern, graph));
            }
            pb.stop();
        } else {
            log.error("Could not compute: no computable patterns found.");
        }
        return computedPatterns;
    }

    private PredictivePattern computeBasicIterationPattern(PredictivePattern predictivePattern, Graph graph) {

        int matchScore;
        int priceVariation;

        if (predictivePattern != null) {

            int startPricePrediction = predictivePattern.getPriceVariationPrediction();
            LocalDateTime startTime = LocalDateTime.now();
//TODO Cast to long? view SonarLint
            int computations = graph.getCandles().size() - predictivePattern.getLength() - predictivePattern.getScope();

            for (var i = 0; i < computations; i++) {
                List<Candle> candlesToMatch = graph.getCandles().subList(i, i + predictivePattern.getLength());
                List<Candle> followingCandles = graph.getCandles().subList(i + predictivePattern.getLength(), i + predictivePattern.getLength() + predictivePattern.getScope());
                List<PixelatedCandle> pixelatedCandlesToMatch = candleFactory.pixelateCandles(candlesToMatch, predictivePattern.getGranularity());
                List<PixelatedCandle> pixelatedFollowingCandles = candleFactory.pixelateCandles(followingCandles, predictivePattern.getGranularity());

                matchScore = calculateMatchScore(predictivePattern, pixelatedCandlesToMatch);

                priceVariation = calculatePriceVariation(predictivePattern, pixelatedFollowingCandles);

                predictivePattern.setPriceVariationPrediction(
                        adjustPriceVariationPrediction(
                                predictivePattern.getPriceVariationPrediction(),
                                matchScore,
                                priceVariation)
                );
            }
            predictivePattern.getComputationsHistory().add(new ComputationData(
                    startTime,
                    LocalDateTime.now(),
                    ComputationType.BASIC_ITERATION,
                    computations,
                    startPricePrediction,
                    predictivePattern.getPriceVariationPrediction()
            ));
        }
        return predictivePattern;
    }

    private int adjustPriceVariationPrediction(int priceVariationPrediction, int matchScore, int priceVariation) {
        return (priceVariationPrediction + priceVariation * matchScore / 100) / 2;
    }

    private int calculatePriceVariation(PredictivePattern pattern, List<PixelatedCandle> pixelatedFollowingCandles) {

        int delta = 101;

        if (pixelatedFollowingCandles.size() >= pattern.getScope()) {
            PixelatedCandle firstCandle = pixelatedFollowingCandles.get(0);
            PixelatedCandle predictionCandle = pixelatedFollowingCandles.get(pattern.getScope() - 1);

            int firstCandleOpenPosition = 0;
            int predictionCandleClosePosition = 0;

            for (var i = 0; i < pattern.getGranularity(); i++) {
                if (firstCandle.candle()[i] == 3) {
                    firstCandleOpenPosition = i + 1;
                }
                if (predictionCandle.candle()[i] == 4) {
                    predictionCandleClosePosition = i + 1;
                }
            }
            delta = Format.relativePercentage(predictionCandleClosePosition - firstCandleOpenPosition, pattern.getGranularity());
        }

        if (delta == 101)   {
            log.error("Unable to calculate price variation: error during the scan of pixelated candles.");
            return 0;
        }

        return delta;
    }

    private int calculateMatchScore(PredictivePattern pattern, List<PixelatedCandle> pixelatedCandlesToMatch) {

        int length = pattern.getLength();
        int granularity = pattern.getGranularity();
        int inkedPixels = 0;
        int matchScore = 0;

        if (pattern.getLength() != pixelatedCandlesToMatch.size())  {
            log.error("Pattern size does not match graph sample size.");
            return 0;
        }

        for (var i = 0; i < length; i++) {

            for (int j = 0; j < granularity; j++) {

                byte patternPixel = pattern.getPixelatedCandles().get(i).candle()[j];

                if (patternPixel != 0)  {
                   inkedPixels++;

                   byte pixelatedCandlesToMatchPixel = pixelatedCandlesToMatch.get(i).candle()[j];

                   if (pixelatedCandlesToMatchPixel != 0)   {
                       matchScore++;
                   }
                }
            }
        }
        return Format.positivePercentage(matchScore, inkedPixels);
    }
}



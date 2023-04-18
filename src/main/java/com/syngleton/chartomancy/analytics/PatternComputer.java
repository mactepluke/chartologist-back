package com.syngleton.chartomancy.analytics;

import com.syngleton.chartomancy.factory.CandleFactory;
import com.syngleton.chartomancy.model.*;
import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class PatternComputer {

    private final CandleFactory candleFactory;

    @Autowired
    public PatternComputer(CandleFactory candleFactory) {
        this.candleFactory = candleFactory;
    }

    public List<Pattern> computeBasicIterationPattern(List<Pattern> patterns, Graph graph) {

        List<Pattern> computedPatterns = new ArrayList<>();

        if (!patterns.isEmpty() && patterns.get(0).getPatternType() == PatternType.PREDICTIVE) {
            ProgressBar pb = new ProgressBar("Compute basic iteration patterns...", patterns.size());

            pb.start();
            for (Pattern pattern : patterns) {
                pb.step();
                computedPatterns.add(computeBasicIterationPattern(pattern, graph));
            }
            pb.stop();
        }
        return computedPatterns;
    }

    private PredictivePattern computeBasicIterationPattern(Pattern pattern, Graph graph) {

        byte matchScore;
        byte priceVariation;
        PredictivePattern predictivePattern = new PredictivePattern((PredictivePattern) pattern);

        if (pattern != null && pattern.getPatternType() == PatternType.PREDICTIVE) {

            ComputationData computationData = new ComputationData();

            computationData.setComputationType(ComputationType.BASIC_ITERATION);
            computationData.setStartPricePrediction(predictivePattern.getPriceVariationPrediction());
            computationData.setStartTime(LocalDateTime.now());

            int maxComputations = graph.candles().size() - predictivePattern.getLength() - predictivePattern.getScope();

            for (var i = 0; i < maxComputations; i++) {
                List<Candle> candlesToMatch = graph.candles().subList(i, i + predictivePattern.getLength());
                List<Candle> followingCandles = graph.candles().subList(i + predictivePattern.getLength(), i + predictivePattern.getLength() + predictivePattern.getScope());
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
                computationData.setComputations(computationData.getComputations() + 1);
            }
            computationData.setEndTime(LocalDateTime.now());
            computationData.setEndPricePrediction(predictivePattern.getPriceVariationPrediction());
            predictivePattern.addComputationsHistory(computationData);
        }
        return predictivePattern;
    }

    private int adjustPriceVariationPrediction(byte priceVariationPrediction, byte matchScore, byte priceVariation) {
        return (priceVariationPrediction + priceVariation * matchScore / 100) / 2;
    }

    private byte calculatePriceVariation(PredictivePattern pattern, List<PixelatedCandle> pixelatedFollowingCandles) {

        byte delta = 101;

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
            delta = Format.byteRelativePercentage(predictionCandleClosePosition - firstCandleOpenPosition, pattern.getGranularity());
        }

        if (delta == 101)   {
            log.error("Unable to calculate price variation: error during the scan of pixelated candles.");
            return 0;
        }

        return delta;
    }

    private byte calculateMatchScore(PredictivePattern pattern, List<PixelatedCandle> pixelatedCandlesToMatch) {

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
                    //TODO variable difficulty/accuracy ?
            }
        }
//TODO variable discriminate wicks from bodys

        //TODO variable threshold?
        return Format.bytePositivePercentage(matchScore, inkedPixels);
    }
}

//TODO variable adjust with volume

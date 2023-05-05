package com.syngleton.chartomancy.analytics;

import com.syngleton.chartomancy.model.charting.candles.FloatCandle;
import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;
import com.syngleton.chartomancy.model.charting.patterns.*;
import com.syngleton.chartomancy.util.Calc;
import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Math.*;

@Log4j2
@Component
public class Analyzer {

    @Value("${match_score_smoothing:NONE}")
    private Smoothing matchScoreSmoothing;
    @Value("${match_score_threshold:0}")
    private int matchScoreThreshold;
    @Value("${price_variation_threshold:0}")
    private int priceVariationThreshold;
    @Value("${extrapolate_price_variation:false}")
    private boolean extrapolatePriceVariation;
    @Value("${extrapolate_match_score:false}")
    private boolean extrapolateMatchScore;

    enum Smoothing {
        NONE,
        LINEAR,
        EXPONENTIAL
    }

    public float calculatePriceVariation(List<FloatCandle> floatFollowingCandles, int scope) {

        if (floatFollowingCandles.size() >= scope) {
            FloatCandle firstCandle = floatFollowingCandles.get(0);
            FloatCandle predictionCandle = floatFollowingCandles.get(scope - 1);

            return filterPricePrediction(Calc.variationPercentage(firstCandle.open(), predictionCandle.close()));

        } else {
            log.error("Unable to calculate price variation prediction : target candles size {} in smaller than scope {}",
                    floatFollowingCandles.size(),
                    scope);
            return 0;
        }
    }

    public int calculateMatchScore(PixelatedPattern pattern, List<PixelatedCandle> pixelatedCandlesToMatch) {

        int length = pattern.getLength();
        int granularity = pattern.getGranularity();
        int inkedPixels = 0;
        int matchScore = 0;

        if (length != pixelatedCandlesToMatch.size()) {
            log.error("Pattern size does not match graph sample size.");
            return 0;
        }

        for (var i = 0; i < length; i++) {

            for (int j = 0; j < granularity; j++) {

                byte patternPixel = pattern.getPixelatedCandles().get(i).candle()[j];

                if (patternPixel != 0) {
                    inkedPixels++;

                    byte pixelatedCandlesToMatchPixel = pixelatedCandlesToMatch.get(i).candle()[j];

                    if (pixelatedCandlesToMatchPixel != 0) {
                        matchScore++;
                    }
                }
            }
        }
        return Calc.positivePercentage(matchScore, inkedPixels);
    }

    public int calculateMatchScore(IntPattern pattern, List<IntCandle> intCandlesToMatch) {

        return calculateMatchScore(pattern.getIntCandles(), intCandlesToMatch);
    }

    public int calculateMatchScore(List<IntCandle> intCandles, List<IntCandle> intCandlesToMatch) {

        int length = intCandles.size();
        double matchScore = 0;
        double patternCandlesSurface = 0;

        if (length != intCandlesToMatch.size()) {
            log.error("Pattern size does not match graph sample size.");
            return 0;
        }

        for (var i = 0; i < length; i++) {

            patternCandlesSurface = patternCandlesSurface + smooth(calculateCandleSurface(intCandles.get(i)), i);

            if (haveSameColor(intCandles.get(i), intCandlesToMatch.get(i))) {

                matchScore = matchScore +
                        smooth(
                                calculateBodyOverlap(intCandles.get(i), intCandlesToMatch.get(i))
                                        + calculateWickOverlap(intCandles.get(i), intCandlesToMatch.get(i)),
                                i
                        );
            }
        }
        return filterMatchScore(Calc.positivePercentage(matchScore, patternCandlesSurface));
    }

    private double smooth(double value, int step) {

        switch (matchScoreSmoothing)    {
            case LINEAR -> {
                return value * (1 + step);
            }
            case EXPONENTIAL -> {
                return value * Math.exp(step);
            }
            default -> {
                return value;
            }
        }
    }

    private int calculateBodyOverlap(IntCandle intCandle, IntCandle intCandleToMatch) {
        return overlapAmount(
                intCandle.open(),
                intCandle.close(),
                intCandleToMatch.open(),
                intCandleToMatch.close());
    }

    private int calculateWickOverlap(IntCandle intCandle, IntCandle intCandleToMatch) {

        if (intCandle.open() > intCandle.close()) {
            return overlapAmount(
                    intCandle.high(),
                    intCandle.open(),
                    intCandleToMatch.high(),
                    intCandleToMatch.open())
                    + overlapAmount(
                    intCandle.close(),
                    intCandle.low(),
                    intCandleToMatch.close(),
                    intCandleToMatch.low());
        } else {
            return overlapAmount(
                    intCandle.low(),
                    intCandle.open(),
                    intCandleToMatch.low(),
                    intCandleToMatch.open())
                    + overlapAmount(
                    intCandle.close(),
                    intCandle.high(),
                    intCandleToMatch.close(),
                    intCandleToMatch.high());
        }
    }

    private int filterMatchScore(int matchScore) {

        if (matchScore < matchScoreThreshold)  {
            return 0;
        }
        if (extrapolateMatchScore)  {
            matchScore = max(matchScore + matchScore * matchScore / 100, 100);
        }
        return matchScore;
    }

    public float filterPricePrediction(float priceVariation) {

        if (abs(priceVariation) < priceVariationThreshold)  {
            return 0;
        }
        if (extrapolatePriceVariation)  {
            priceVariation = Format.streamline(priceVariation + priceVariation * abs(priceVariation) / 100, -100, 100);
        }
        return priceVariation;
    }

    private boolean haveSameColor(IntCandle intCandle, IntCandle intCandleToMatch) {

        return (intCandle.open() < intCandle.close()
                && intCandleToMatch.open() < intCandleToMatch.close())
                || (intCandle.open() > intCandle.close()
                && intCandleToMatch.open() > intCandleToMatch.close());
    }

    private int calculateCandleSurface(IntCandle intCandle) {

        return abs(intCandle.open() - intCandle.close())
                + (intCandle.open() > intCandle.close() ?
                intCandle.close() - intCandle.low() + intCandle.high() - intCandle.open()
                : intCandle.open() - intCandle.low() + intCandle.high() - intCandle.close());
    }

    private int overlapAmount(int aStart, int aEnd, int bStart, int bEnd) {

        if (bStart > aEnd || aStart > bEnd) {
            return 0;
        } else {
            return min(aEnd, bEnd) - max(aStart, bStart);
        }
    }
}

package co.syngleton.chartomancer.analytics;

import co.syngleton.chartomancer.shared_domain.FloatCandle;
import co.syngleton.chartomancer.shared_domain.IntCandle;
import co.syngleton.chartomancer.util.Calc;
import co.syngleton.chartomancer.util.Format;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static java.lang.Math.*;

@Log4j2
record AnalyzerImpl(Smoothing matchScoreSmoothing,
                    int matchScoreThreshold,
                    int priceVariationThreshold,
                    boolean extrapolatePriceVariation,
                    boolean extrapolateMatchScore) implements Analyzer {

    @Override
    public float calculatePriceVariation(List<FloatCandle> floatFollowingCandles, int scope) {

        if (floatFollowingCandles.size() < scope) {
            log.error("Unable to calculate price variation: target candles size {} in smaller than scope {}",
                    floatFollowingCandles.size(),
                    scope);
            return 0;
        }

        FloatCandle firstCandle = floatFollowingCandles.get(0);
        FloatCandle predictionCandle = floatFollowingCandles.get(scope - 1);

        return Calc.variationPercentage(firstCandle.open(), predictionCandle.close());
    }

    @Override
    public float filterPriceVariation(float priceVariation) {

        if (abs(priceVariation) < priceVariationThreshold) {
            return 0;
        }
        if (extrapolatePriceVariation) {
            priceVariation = extrapolate(priceVariation, -100, 100);
        }
        return priceVariation;
    }

    private float extrapolate(float value, float min, float max) {
        return Format.streamline(value + value * abs(value) / 100, min, max);
    }

    @Override
    public int calculateMatchScore(List<IntCandle> intCandles, List<IntCandle> intCandlesToMatch) {

        int length = intCandles.size();
        double matchScore = 0;
        double patternCandlesSurface = 0;

        if (length != intCandlesToMatch.size()) {
            log.error("Pattern size does not match graph sample size.");
            return 0;
        }

        for (var i = 0; i < length; i++) {

            IntCandle candle = intCandles.get(i);
            IntCandle candleToMatch = intCandlesToMatch.get(i);

            patternCandlesSurface = patternCandlesSurface + smooth(calculateCandleSurface(candle), i);

            if (haveSameColor(candle, candleToMatch)) {

                double candleSurfaceOverlap = (double) calculateBodyOverlap(candle, candleToMatch) + calculateWickOverlap(candle, candleToMatch);

                matchScore = matchScore +
                        smooth(candleSurfaceOverlap, i);
            }
        }
        return filterMatchScore(Calc.positivePercentage(matchScore, patternCandlesSurface));
    }

    private int filterMatchScore(int matchScore) {

        if (matchScore < matchScoreThreshold) {
            return 0;
        }

        if (extrapolateMatchScore) {
            matchScore = (int) extrapolate(matchScore, 0, 100);
        }
        return matchScore;
    }

    private double smooth(double value, int step) {

        switch (matchScoreSmoothing) {
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

    private int calculateCandleSurface(IntCandle intCandle) {

        return abs(intCandle.open() - intCandle.close())
                + (intCandle.open() > intCandle.close() ?
                intCandle.close() - intCandle.low() + intCandle.high() - intCandle.open()
                : intCandle.open() - intCandle.low() + intCandle.high() - intCandle.close());
    }

    private boolean haveSameColor(IntCandle intCandle, IntCandle intCandleToMatch) {

        return (intCandle.open() < intCandle.close()
                && intCandleToMatch.open() < intCandleToMatch.close())
                || (intCandle.open() > intCandle.close()
                && intCandleToMatch.open() > intCandleToMatch.close());
    }

    private int calculateBodyOverlap(IntCandle intCandle, IntCandle intCandleToMatch) {
        return overlapAmount(
                intCandle.open(),
                intCandle.close(),
                intCandleToMatch.open(),
                intCandleToMatch.close());
    }

    private int calculateWickOverlap(IntCandle intCandle, IntCandle intCandleToMatch) {

        if (isRedCandle(intCandle)) {
            return calculateRedCandleWickOverlap(intCandle, intCandleToMatch);
        } else {
            return calculateGreenCandleWickOverlap(intCandle, intCandleToMatch);
        }
    }

    private boolean isRedCandle(IntCandle intCandle) {
        return intCandle.open() > intCandle.close();
    }

    private int calculateRedCandleWickOverlap(IntCandle intCandle, IntCandle intCandleToMatch) {
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
    }

    private int calculateGreenCandleWickOverlap(IntCandle intCandle, IntCandle intCandleToMatch) {
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

    private int overlapAmount(int aStart, int aEnd, int bStart, int bEnd) {

        if (bStart > aEnd || aStart > bEnd) {
            return 0;
        } else {
            return min(aEnd, bEnd) - max(aStart, bStart);
        }
    }
}
package co.syngleton.chartomancer.analytics;

import co.syngleton.chartomancer.core_entities.CoreDataSettingNames;
import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.IntCandle;
import co.syngleton.chartomancer.util.Calc;
import co.syngleton.chartomancer.util.Format;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

        if (areNotSameSize(intCandles, intCandlesToMatch)) {
            return 0;
        }

        double surfaceMatch = calculateSurfaceMatch(intCandles, intCandlesToMatch);
        int candlesSpan = calculateSurfaceSpan(intCandles, intCandlesToMatch);

        return filterMatchScore(Calc.positivePercentage(surfaceMatch, candlesSpan));
    }

    @Override
    public Map<CoreDataSettingNames, String> getSettingsSnapshot() {
        return Map.of(
                CoreDataSettingNames.MATCH_SCORE_SMOOTHING, matchScoreSmoothing.toString(),
                CoreDataSettingNames.MATCH_SCORE_THRESHOLD, String.valueOf(matchScoreThreshold),
                CoreDataSettingNames.PRICE_VARIATION_THRESHOLD, String.valueOf(priceVariationThreshold),
                CoreDataSettingNames.EXTRAPOLATE_PRICE_VARIATION, String.valueOf(extrapolatePriceVariation),
                CoreDataSettingNames.EXTRAPOLATE_MATCH_SCORE, String.valueOf(extrapolateMatchScore)
        );
    }

    private boolean areNotSameSize(List<IntCandle> intCandles1, List<IntCandle> intCandles2) {
        return intCandles1.size() != intCandles2.size();
    }

    double calculateSurfaceMatch(List<IntCandle> intCandles1, List<IntCandle> intCandles2) {
        double surfaceMatch = 0;

        for (var i = 0; i < intCandles1.size(); i++) {

            IntCandle candle = intCandles1.get(i);
            IntCandle candleToMatch = intCandles2.get(i);

            int candleSurfaceOverlap = calculateCandleOverlap(candle, candleToMatch);
            surfaceMatch += smooth(candleSurfaceOverlap, i);
        }

        return surfaceMatch;
    }

    int calculateCandleOverlap(IntCandle candle, IntCandle candleToMatch) {
        return calculateSameSideWickOverlap(candle, candleToMatch) + calculateSameColorBodyOverlap(candle, candleToMatch);
    }

    int calculateSurfaceSpan(List<IntCandle> intCandles1, List<IntCandle> intCandles2) {

        if (areNotSameSize(intCandles1, intCandles2)) {
            return 0;
        }

        int span = 0;

        for (var i = 0; i < intCandles1.size(); i++) {
            int lowest = min(intCandles1.get(i).low(), intCandles2.get(i).low());
            int highest = max(intCandles1.get(i).high(), intCandles2.get(i).high());
            span += smooth(highest - lowest, i);
        }

        return span;
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

    private int smooth(int value, int step) {

        switch (matchScoreSmoothing) {
            case LOGARITHMIC -> {
                return (int) round(value * Math.log((double) 2 + step));
            }
            case LINEAR -> {
                return value * (1 + step);
            }
            case EXPONENTIAL -> {
                return (int) round(value * Math.exp(step));
            }
            default -> {
                return value;
            }
        }
    }

    int calculateCandleSurface(IntCandle intCandle) {

        int[] candleValues = {intCandle.open(), intCandle.close(), intCandle.high(), intCandle.low()};
        Arrays.sort(candleValues);

        return candleValues[3] - candleValues[0];
    }

    boolean haveSameColor(IntCandle intCandle, IntCandle intCandleToMatch) {
        return (isEmptyBodied(intCandle) || isEmptyBodied(intCandleToMatch) || isGreen(intCandle) == isGreen(intCandleToMatch));
    }

    private boolean isGreen(IntCandle intCandle) {
        return intCandle.open() <= intCandle.close();
    }

    private boolean isEmptyBodied(IntCandle intCandle) {
        return intCandle.open() == intCandle.close();
    }

    int calculateSameColorBodyOverlap(IntCandle intCandle, IntCandle intCandleToMatch) {

        if (!haveSameColor(intCandle, intCandleToMatch)) {
            return 0;
        }

        return calculateBodyOverlap(intCandle, intCandleToMatch);
    }

    int calculateBodyOverlap(IntCandle intCandle, IntCandle intCandleToMatch) {

        return overlapAmount(
                intCandle.open(),
                intCandle.close(),
                intCandleToMatch.open(),
                intCandleToMatch.close());
    }

    int calculateSameSideWickOverlap(IntCandle intCandle, IntCandle intCandleToMatch) {

        int[] intCandleValues = {intCandle.open(), intCandle.close(), intCandle.high(), intCandle.low()};
        int[] intCandleToMatchValues = {intCandleToMatch.open(), intCandleToMatch.close(), intCandleToMatch.high(), intCandleToMatch.low()};

        Arrays.sort(intCandleValues);
        Arrays.sort(intCandleToMatchValues);

        return overlapAmount(intCandleValues[0], intCandleValues[1], intCandleToMatchValues[0], intCandleToMatchValues[1])
                + overlapAmount(intCandleValues[2], intCandleValues[3], intCandleToMatchValues[2], intCandleToMatchValues[3]);

    }

    int overlapAmount(int aStart, int aEnd, int bStart, int bEnd) {

        if ((min(aStart, aEnd) > max(bStart, bEnd)) || (max(aStart, aEnd) < min(bStart, bEnd))) {
            return 0;
        }

        int[] values = {aStart, aEnd, bStart, bEnd};
        Arrays.sort(values);

        return abs(values[1] - values[2]);
    }

}


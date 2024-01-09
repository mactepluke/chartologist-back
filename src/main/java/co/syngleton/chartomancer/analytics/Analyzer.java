package co.syngleton.chartomancer.analytics;

import co.syngleton.chartomancer.domain.FloatCandle;
import co.syngleton.chartomancer.domain.IntCandle;

import java.util.List;

public interface Analyzer {
    float calculatePriceVariation(List<FloatCandle> floatFollowingCandles, int scope);

    float filterPriceVariation(float priceVariation);

    int calculateMatchScore(List<IntCandle> intCandles, List<IntCandle> intCandlesToMatch);

    Smoothing getMatchScoreSmoothing();

    int getMatchScoreThreshold();

    int getPriceVariationThreshold();

    boolean isExtrapolatePriceVariation();

    boolean isExtrapolateMatchScore();
}

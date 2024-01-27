package co.syngleton.chartomancer.analytics;

import co.syngleton.chartomancer.shared_domain.FloatCandle;
import co.syngleton.chartomancer.shared_domain.IntCandle;

import java.util.List;

public interface Analyzer {

    static Analyzer getNewInstance(Smoothing matchScoreSmoothing,
                                   int matchScoreThreshold,
                                   int priceVariationThreshold,
                                   boolean extrapolatePriceVariation,
                                   boolean extrapolateMatchScore) {
        return new AnalyzerImpl(matchScoreSmoothing, matchScoreThreshold, priceVariationThreshold, extrapolatePriceVariation, extrapolateMatchScore);
    }

    float calculatePriceVariation(List<FloatCandle> floatFollowingCandles, int scope);

    float filterPriceVariation(float priceVariation);

    int calculateMatchScore(List<IntCandle> intCandles, List<IntCandle> intCandlesToMatch);

    Smoothing matchScoreSmoothing();

    int matchScoreThreshold();

    int priceVariationThreshold();

    boolean extrapolatePriceVariation();

    boolean extrapolateMatchScore();
}

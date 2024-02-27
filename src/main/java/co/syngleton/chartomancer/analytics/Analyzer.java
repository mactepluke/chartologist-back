package co.syngleton.chartomancer.analytics;

import co.syngleton.chartomancer.core_entities.CoreDataSettingNames;
import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.IntCandle;

import java.util.List;
import java.util.Map;


public interface Analyzer extends PatternRecognitionAnalyzer, TradingAnalyzer {

    static Analyzer getNewInstance(Smoothing matchScoreSmoothing,
                                   int matchScoreThreshold,
                                   int priceVariationThreshold,
                                   boolean extrapolatePriceVariation,
                                   boolean extrapolateMatchScore) {
        return new DefaultAnalyzer(matchScoreSmoothing, matchScoreThreshold, priceVariationThreshold, extrapolatePriceVariation, extrapolateMatchScore);
    }

    @Override
    float calculatePriceVariation(List<FloatCandle> floatFollowingCandles, int scope);

    @Override
    float filterPriceVariation(float priceVariation);

    @Override
    int calculateMatchScore(List<IntCandle> intCandles, List<IntCandle> intCandlesToMatch);

    @Override
    Map<CoreDataSettingNames, String> getSettingsSnapshot();

}

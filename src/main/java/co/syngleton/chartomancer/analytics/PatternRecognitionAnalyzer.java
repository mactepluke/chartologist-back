package co.syngleton.chartomancer.analytics;

import co.syngleton.chartomancer.core_entities.CoreDataSettingNames;
import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.IntCandle;

import java.util.List;
import java.util.Map;

public interface PatternRecognitionAnalyzer extends TradingAnalyzer {

    float calculatePriceVariation(List<FloatCandle> floatFollowingCandles, int scope);

    @Override
    float filterPriceVariation(float priceVariation);

    @Override
    int calculateMatchScore(List<IntCandle> intCandles, List<IntCandle> intCandlesToMatch);

    @Override
    Map<CoreDataSettingNames, String> getSettingsSnapshot();
}

package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.core_entities.CoreDataSettingNames;
import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.IntCandle;

import java.util.List;
import java.util.Map;

public interface PatternRecognitionAnalyzer {

    float calculatePriceVariation(List<FloatCandle> floatFollowingCandles, int scope);

    float filterPriceVariation(float priceVariation);

    int calculateMatchScore(List<IntCandle> intCandles, List<IntCandle> intCandlesToMatch);

    Map<CoreDataSettingNames, String> getSettingsSnapshot();
}

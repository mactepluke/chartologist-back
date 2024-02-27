package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.core_entities.CoreDataSettingNames;
import co.syngleton.chartomancer.core_entities.IntCandle;

import java.util.List;
import java.util.Map;

public interface TradingAnalyzer {

    float filterPriceVariation(float priceVariation);

    int calculateMatchScore(List<IntCandle> intCandles, List<IntCandle> intCandlesToMatch);

    Map<CoreDataSettingNames, String> getSettingsSnapshot();
}

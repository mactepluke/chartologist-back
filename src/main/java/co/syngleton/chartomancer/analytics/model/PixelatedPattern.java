package co.syngleton.chartomancer.analytics.model;

import java.util.List;

public sealed interface PixelatedPattern permits ObsoleteBasicPattern, ObsoletePredictivePattern, ObsoleteTradingPattern {
    List<PixelatedCandle> getPixelatedCandles();

    int getGranularity();

    int getLength();
}

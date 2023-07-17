package co.syngleton.chartomancer.model.charting.patterns.light;

import co.syngleton.chartomancer.model.charting.candles.IntCandle;

import java.util.List;

public sealed interface IntPattern permits LightBasicPattern, LightPredictivePattern, LightTradingPattern {

    List<IntCandle> getIntCandles();

    int getGranularity();

    int getLength();
}

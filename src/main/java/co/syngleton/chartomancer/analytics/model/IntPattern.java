package co.syngleton.chartomancer.analytics.model;

import java.util.List;

public sealed interface IntPattern permits BasicPattern, PredictivePattern, TradingPattern {

    List<IntCandle> getIntCandles();

    int getGranularity();

    int getLength();
}

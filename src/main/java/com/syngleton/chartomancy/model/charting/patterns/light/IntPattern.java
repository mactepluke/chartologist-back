package com.syngleton.chartomancy.model.charting.patterns.light;

import com.syngleton.chartomancy.model.charting.candles.IntCandle;

import java.util.List;

public sealed interface IntPattern permits LightBasicPattern, LightPredictivePattern, LightTradingPattern {

    List<IntCandle> getIntCandles();

    int getGranularity();

    int getLength();
}

package com.syngleton.chartomancy.model.charting.patterns.pixelated;

import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;

import java.util.List;

public sealed interface PixelatedPattern permits BasicPattern, PredictivePattern, TradingPattern {
    List<PixelatedCandle> getPixelatedCandles();

    int getGranularity();

    int getLength();
}

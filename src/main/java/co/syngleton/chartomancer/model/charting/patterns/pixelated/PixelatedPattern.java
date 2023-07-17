package co.syngleton.chartomancer.model.charting.patterns.pixelated;

import co.syngleton.chartomancer.model.charting.candles.PixelatedCandle;

import java.util.List;

public sealed interface PixelatedPattern permits BasicPattern, PredictivePattern, TradingPattern {
    List<PixelatedCandle> getPixelatedCandles();

    int getGranularity();

    int getLength();
}

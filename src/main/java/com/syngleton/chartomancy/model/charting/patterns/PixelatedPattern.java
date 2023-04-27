package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;

import java.util.List;

public interface PixelatedPattern {
    List<PixelatedCandle> getPixelatedCandles();
    int getGranularity();
}

package com.syngleton.chartomancy.model.charting.patterns.interfaces;

import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;

import java.util.List;

public interface PixelatedPattern {
    List<PixelatedCandle> getPixelatedCandles();
    int getGranularity();

    int getLength();
}

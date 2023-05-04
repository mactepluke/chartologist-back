package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.model.charting.candles.IntCandle;

import java.util.List;

public interface IntPattern {

        List<IntCandle> getIntCandles();

        int getGranularity();

        int getLength();
}

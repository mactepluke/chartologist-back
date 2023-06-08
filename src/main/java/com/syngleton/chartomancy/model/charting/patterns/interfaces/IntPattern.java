package com.syngleton.chartomancy.model.charting.patterns.interfaces;

import com.syngleton.chartomancy.model.charting.candles.IntCandle;

import java.util.List;

public interface IntPattern {

        List<IntCandle> getIntCandles();

        int getGranularity();

        int getLength();
}

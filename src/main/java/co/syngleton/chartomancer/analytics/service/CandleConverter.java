package co.syngleton.chartomancer.analytics.service;

import co.syngleton.chartomancer.analytics.model.FloatCandle;
import co.syngleton.chartomancer.analytics.model.IntCandle;

import java.util.List;

public interface CandleConverter {
    List<IntCandle> rescaleToIntCandles(List<FloatCandle> floatCandles, int granularity);
}

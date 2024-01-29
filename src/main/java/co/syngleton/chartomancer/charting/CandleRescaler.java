package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.IntCandle;

import java.util.List;

public interface CandleRescaler {
    List<IntCandle> rescale(List<FloatCandle> floatCandles, int granularity);
}

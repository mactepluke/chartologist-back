package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.shared_domain.FloatCandle;
import co.syngleton.chartomancer.shared_domain.IntCandle;

import java.util.List;

public interface CandleRescaler {
    List<IntCandle> rescale(List<FloatCandle> floatCandles, int granularity);
}

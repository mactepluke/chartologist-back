package co.syngleton.chartomancer.contracts;

import co.syngleton.chartomancer.analytics.model.FloatCandle;
import co.syngleton.chartomancer.analytics.model.IntCandle;

import java.util.List;

public interface CandleNormalizer {
    List<IntCandle> normalizeCandles(List<FloatCandle> floatCandles, int granularity);
}

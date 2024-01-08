package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.domain.FloatCandle;
import co.syngleton.chartomancer.domain.IntCandle;

import java.util.List;

public interface CandleNormalizer {
    List<IntCandle> normalizeCandles(List<FloatCandle> floatCandles, int granularity);
}

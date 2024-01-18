package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.shared_domain.FloatCandle;
import co.syngleton.chartomancer.shared_domain.IntCandle;

import java.util.List;

public interface CandleNormalizer {
    List<IntCandle> normalizeCandles(List<FloatCandle> floatCandles, int granularity);
}

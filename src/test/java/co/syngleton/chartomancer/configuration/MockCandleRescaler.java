package co.syngleton.chartomancer.configuration;

import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.IntCandle;

import java.util.Collections;
import java.util.List;

public class MockCandleRescaler implements CandleRescaler {

    @Override
    public List<IntCandle> rescale(List<FloatCandle> floatCandles, int granularity) {
        return Collections.emptyList();
    }
}

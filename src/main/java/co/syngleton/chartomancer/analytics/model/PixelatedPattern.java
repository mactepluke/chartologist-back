package co.syngleton.chartomancer.analytics.model;

import java.util.List;

public interface PixelatedPattern {
    List<PixelatedCandle> getPixelatedCandles();

    int getGranularity();

    int getLength();
}

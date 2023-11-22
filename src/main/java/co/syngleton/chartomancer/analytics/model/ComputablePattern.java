package co.syngleton.chartomancer.analytics.model;

import java.util.List;

public interface ComputablePattern extends ScopedPattern {

    List<IntCandle> getIntCandles();

    int getLength();

    int getGranularity();

    void setPriceVariationPrediction(float priceVariationPrediction);
}

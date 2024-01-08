package co.syngleton.chartomancer.model;

import java.util.List;

public interface ComputablePattern extends ScopedPattern {

    int getLength();

    int getGranularity();

    void setPriceVariationPrediction(float priceVariationPrediction);

    List<IntCandle> getIntCandles();

}

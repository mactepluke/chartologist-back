package co.syngleton.chartomancer.analytics.model;

public interface ComputablePattern extends ScopedPattern {

    int getLength();

    int getGranularity();

    void setPriceVariationPrediction(float priceVariationPrediction);
}

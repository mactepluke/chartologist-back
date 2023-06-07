package com.syngleton.chartomancy.model.charting.patterns;

public interface ComputablePattern extends ScopedPattern {

    PatternType getPatternType();
    float getPriceVariationPrediction();

    int getLength();

    int getGranularity();

    void setPriceVariationPrediction(float priceVariationPrediction);
}

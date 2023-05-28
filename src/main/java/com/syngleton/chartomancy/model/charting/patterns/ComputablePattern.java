package com.syngleton.chartomancy.model.charting.patterns;

public interface ComputablePattern {

    PatternType getPatternType();
    float getPriceVariationPrediction();

    int getScope();

    int getLength();

    int getGranularity();

    void setPriceVariationPrediction(float priceVariationPrediction);
}

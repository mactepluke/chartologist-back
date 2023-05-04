package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.analytics.ComputationData;

import java.util.List;

public interface ComputablePattern {

    PatternType getPatternType();
    float getPriceVariationPrediction();

    int getScope();

    int getLength();

    int getGranularity();

    List<ComputationData> getComputationsHistory();

    void setPriceVariationPrediction(float priceVariationPrediction);
}

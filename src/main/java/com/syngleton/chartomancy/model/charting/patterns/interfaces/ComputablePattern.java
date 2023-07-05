package com.syngleton.chartomancy.model.charting.patterns.interfaces;

import com.syngleton.chartomancy.model.charting.misc.PatternType;

public interface ComputablePattern extends ScopedPattern {

    PatternType getPatternType();

    int getLength();

    int getGranularity();

    void setPriceVariationPrediction(float priceVariationPrediction);
}

package co.syngleton.chartomancer.model.charting.patterns.interfaces;

import co.syngleton.chartomancer.model.charting.misc.PatternType;

public interface ComputablePattern extends ScopedPattern {

    PatternType getPatternType();

    int getLength();

    int getGranularity();

    void setPriceVariationPrediction(float priceVariationPrediction);
}

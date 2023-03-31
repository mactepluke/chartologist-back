package com.syngleton.chartomancy.model.patterns;

public class PredictivePatternFactory extends PatternFactory {
    @Override
    protected Pattern createPattern() {
        return new PredictivePattern();
    }
}

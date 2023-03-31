package com.syngleton.chartomancy.model.patterns;

public class BasicPatternFactory extends PatternFactory {
    @Override
    protected Pattern createPattern() {
        return new BasicPattern();
    }
}

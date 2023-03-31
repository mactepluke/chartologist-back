package com.syngleton.chartomancy.model.patterns;

public abstract class PatternFactory {
    public Pattern create() {
        Pattern pattern = createPattern();
        pattern.build();
        return pattern;
    }
    protected abstract Pattern createPattern();
}

package com.syngleton.chartomancy.model.charting;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

public class PatternBox extends ChartObject {

    @Getter
    private final List<Pattern> patterns;

    public PatternBox(Symbol symbol, Timeframe timeframe, List<Pattern> patterns) {
        super(symbol, timeframe);
        this.patterns = patterns;
    }

    @Override
    public String toString()  {
        return "PatternBox{" +
                ", symbol=" + getSymbol() +
                ", timeframe=" + getTimeframe() + "}";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.matches((ChartObject) o);
    }

    @Override
    public int hashCode()  {
        int result = Objects.hash(getSymbol());
        result = 31 * result + Objects.hash(getTimeframe());
        return result;
    }
}

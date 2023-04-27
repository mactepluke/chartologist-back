package com.syngleton.chartomancy.model.charting.misc;

import com.syngleton.chartomancy.model.charting.candles.FloatCandle;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class Graph extends ChartObject {
    private final String name;
    private final List<FloatCandle> floatCandles;

    public Graph(String name, Symbol symbol, Timeframe timeframe, List<FloatCandle> floatCandles) {
        super(symbol, timeframe);
        this.name = name;
        this.floatCandles = floatCandles;
    }

    @Override
    public String toString()  {
        return "Graph{" +
                "name=" + name +
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

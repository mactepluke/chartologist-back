package com.syngleton.chartomancy.model.charting;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class Graph extends ChartObject {
    private final String name;
    private final List<Candle> candles;

    public Graph(String name, Symbol symbol, Timeframe timeframe, List<Candle> candles) {
        super(symbol, timeframe);
        this.name = name;
        this.candles = candles;
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

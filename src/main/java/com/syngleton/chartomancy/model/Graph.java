package com.syngleton.chartomancy.model;

import lombok.Getter;

import java.util.List;

@Getter
public class Graph extends ChartObject {
    private final String name;
    private final List<Candle> candles;

    public Graph(String name, Symbol symbol, Timeframe timeframe, List<Candle> candles) {
        super(symbol, timeframe);
        this.name = name;
        this.candles = candles;
    }
}

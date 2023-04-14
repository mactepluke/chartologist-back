package com.syngleton.chartomancy.model;

import java.util.List;

public record Graph(String name, String symbol, Timeframe timeframe, List<Candle> candles) {
}

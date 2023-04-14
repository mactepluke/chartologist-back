package com.syngleton.chartomancy.model.dataloading;

import java.util.List;

public record Graph(String name, String symbol, Timeframe timeframe, List<Candle> candles) {
}

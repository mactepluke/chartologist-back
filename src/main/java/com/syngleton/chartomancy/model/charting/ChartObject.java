package com.syngleton.chartomancy.model.charting;

import lombok.Getter;

@Getter
public abstract class ChartObject {
    private final Symbol symbol;
    private final Timeframe timeframe;

    protected ChartObject(Symbol symbol, Timeframe timeframe) {
        this.symbol = symbol;
        this.timeframe = timeframe;
    }

    public boolean matches(ChartObject chartObject)    {
        return (chartObject.timeframe == this.timeframe) && (chartObject.symbol == this.symbol);
    }
}

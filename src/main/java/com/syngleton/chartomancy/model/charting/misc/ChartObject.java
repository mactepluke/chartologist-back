package com.syngleton.chartomancy.model.charting.misc;

import com.syngleton.chartomancy.util.Check;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@Getter
@ToString
public abstract class ChartObject {
    private final Symbol symbol;
    private final Timeframe timeframe;

    protected ChartObject() {
        this.symbol = null;
        this.timeframe = null;
    }

    protected ChartObject(Symbol symbol, Timeframe timeframe) {
        this.symbol = symbol;
        this.timeframe = timeframe;
    }

    protected ChartObject(ChartObject chartObject) {
        this.symbol = chartObject == null ? null : chartObject.getSymbol();
        this.timeframe = chartObject == null ? null : chartObject.getTimeframe();
    }

    public boolean matches(ChartObject chartObject)    {
        return chartObject != null
                && chartObject.timeframe != null
                && chartObject.timeframe == this.timeframe
                && chartObject.symbol != null
                && chartObject.symbol == this.symbol;
    }

    public <T> boolean doesNotMatchAnyChartObjectIn(Collection<T> chartObjectCollection) {
        if (Check.notNullNotEmpty(chartObjectCollection)) {
            for (T collectionObject : chartObjectCollection) {
                if ((this.matches((ChartObject) collectionObject))) {
                    return false;
                }
            }
        }
        return true;
    }

    public <T> T getFirstMatchingChartObjectIn(Collection<T> chartObjectCollection) {
        if (Check.notNullNotEmpty(chartObjectCollection)) {
            for (T collectionObject : chartObjectCollection) {
                if ((this.matches((ChartObject) collectionObject))) {
                    return collectionObject;
                }
            }
        }
        return null;
    }

}
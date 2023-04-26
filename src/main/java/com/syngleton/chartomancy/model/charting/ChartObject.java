package com.syngleton.chartomancy.model.charting;

import com.syngleton.chartomancy.util.Check;
import lombok.Getter;

import java.util.Collection;

@Getter
public abstract class ChartObject {
    private final Symbol symbol;
    private final Timeframe timeframe;

    protected ChartObject(Symbol symbol, Timeframe timeframe) {
        this.symbol = symbol;
        this.timeframe = timeframe;
    }

    public boolean matches(ChartObject chartObject)    {
        return chartObject != null
                && chartObject.timeframe != null
                && chartObject.timeframe == this.timeframe
                && chartObject.symbol != null
                && chartObject.symbol == this.symbol;
    }

    public <T> boolean matchesAnyChartObjectIn(Collection<T> chartObjectCollection) {
        if (Check.notNullNotEmpty(chartObjectCollection)) {
            for (T collectionObject : chartObjectCollection) {
                if ((this.matches((ChartObject) collectionObject))) {
                    return true;
                }
            }
        }
        return false;
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

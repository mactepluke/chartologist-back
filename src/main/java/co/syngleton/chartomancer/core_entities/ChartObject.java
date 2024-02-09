package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.util.Check;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;

@Getter
@ToString
public abstract class ChartObject implements Serializable {
    private final Symbol symbol;
    private final Timeframe timeframe;

    protected ChartObject(Symbol symbol, Timeframe timeframe) {
        this.symbol = symbol == null ? Symbol.UNDEFINED : symbol;
        this.timeframe = timeframe == null ? Timeframe.UNKNOWN : timeframe;
    }

    protected ChartObject(ChartObject chartObject) {
        this.symbol = chartObject == null ? Symbol.UNDEFINED : chartObject.getSymbol();
        this.timeframe = chartObject == null ? Timeframe.UNKNOWN : chartObject.getTimeframe();
    }

    public final boolean matches(ChartObject chartObject) {
        return chartObject != null
                && chartObject.timeframe != null
                && chartObject.timeframe == this.timeframe
                && chartObject.symbol != null
                && chartObject.symbol == this.symbol;
    }

    public final <T> boolean doesNotMatchAnyChartObjectIn(Collection<T> chartObjectCollection) {
        if (Check.isNotEmpty(chartObjectCollection)) {
            for (T collectionObject : chartObjectCollection) {
                if ((this.matches((ChartObject) collectionObject))) {
                    return false;
                }
            }
        }
        return true;
    }

    public final <T> T getFirstMatchingChartObjectIn(Collection<T> chartObjectCollection) {
        if (Check.isNotEmpty(chartObjectCollection)) {
            for (T collectionObject : chartObjectCollection) {
                if ((this.matches((ChartObject) collectionObject))) {
                    return collectionObject;
                }
            }
        }
        return null;
    }

}

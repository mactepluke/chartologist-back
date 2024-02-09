package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@ToString(callSuper = true)
@Getter
public abstract class Pattern extends ChartObject {
    private final int granularity;
    @ToString.Exclude
    private final List<IntCandle> intCandles;

    Pattern(int granularity,
            Symbol symbol,
            Timeframe timeframe,
            List<IntCandle> intCandles
    ) {
        super(symbol, timeframe);
        this.granularity = granularity;
        this.intCandles = Collections.unmodifiableList(intCandles);
    }

    public int getLength() {
        return intCandles.size();
    }
}

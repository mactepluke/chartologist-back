package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BasicPattern extends Pattern {

    public BasicPattern(
            List<IntCandle> intCandles,
            int granularity,
            Symbol symbol,
            Timeframe timeframe
    ) {
        super(
                granularity,
                symbol,
                timeframe,
                intCandles
        );
    }
}

package co.syngleton.chartomancer.model.charting.patterns;

import co.syngleton.chartomancer.model.charting.misc.ChartObject;
import co.syngleton.chartomancer.model.charting.misc.PatternType;
import co.syngleton.chartomancer.model.charting.misc.Symbol;
import co.syngleton.chartomancer.model.charting.misc.Timeframe;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public abstract class Pattern extends ChartObject {
    private final PatternType patternType;
    private final int granularity;
    private final int length;

    protected Pattern(PatternType patternType,
                      int granularity,
                      int length,
                      Symbol symbol,
                      Timeframe timeframe
    ) {
        super(symbol, timeframe);
        this.patternType = patternType;
        this.granularity = granularity;
        this.length = length;
    }
}

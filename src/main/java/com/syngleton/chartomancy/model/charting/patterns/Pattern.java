package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.model.charting.misc.ChartObject;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

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

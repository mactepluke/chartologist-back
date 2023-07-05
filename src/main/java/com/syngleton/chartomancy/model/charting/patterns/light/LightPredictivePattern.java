package com.syngleton.chartomancy.model.charting.patterns.light;

import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import com.syngleton.chartomancy.model.charting.misc.PatternType;
import com.syngleton.chartomancy.model.charting.patterns.Pattern;
import com.syngleton.chartomancy.model.charting.patterns.interfaces.ComputablePattern;
import com.syngleton.chartomancy.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class LightPredictivePattern extends Pattern implements ComputablePattern, IntPattern {

    @ToString.Exclude
    private final List<IntCandle> intCandles;
    private final int scope;
    private final LocalDateTime startDate;
    @Setter
    private float priceVariationPrediction = 0;

    public LightPredictivePattern(LightBasicPattern pattern, int scope) {
        super(
                PatternType.LIGHT_PREDICTIVE,
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe()
        );
        this.intCandles = pattern.getIntCandles();
        this.startDate = pattern.getStartDate();
        this.scope = Format.streamline(scope, 1, this.getLength());
    }

}
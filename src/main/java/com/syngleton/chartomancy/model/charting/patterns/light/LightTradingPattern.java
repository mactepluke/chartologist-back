package com.syngleton.chartomancy.model.charting.patterns.light;

import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import com.syngleton.chartomancy.model.charting.misc.PatternType;
import com.syngleton.chartomancy.model.charting.patterns.Pattern;
import com.syngleton.chartomancy.model.charting.patterns.interfaces.ScopedPattern;
import com.syngleton.chartomancy.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class LightTradingPattern extends Pattern implements IntPattern, ScopedPattern {

    @ToString.Exclude
    private final List<IntCandle> intCandles;
    private final int scope;
    private final float priceVariationPrediction;

    public LightTradingPattern(LightPredictivePattern pattern) {
        super(
                PatternType.LIGHT_TRADING,
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe()
        );
        this.intCandles = pattern.getIntCandles();
        this.scope = pattern.getScope();
        this.priceVariationPrediction = Format.roundTwoDigits(pattern.getPriceVariationPrediction());
    }
}

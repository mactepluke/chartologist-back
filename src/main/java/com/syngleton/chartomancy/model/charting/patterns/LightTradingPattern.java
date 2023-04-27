package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
public class LightTradingPattern extends Pattern    {

    @ToString.Exclude
    private final List<IntCandle> intCandles;
    private final int scope;
    private final int priceVariationPrediction;

    public LightTradingPattern(LightPredictivePattern pattern) {
        super(
                PatternType.TRADING,
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe()
        );
        this.intCandles = pattern.getIntCandles();
        this.scope = pattern.getScope();
        this.priceVariationPrediction = pattern.getPriceVariationPrediction();
    }
}

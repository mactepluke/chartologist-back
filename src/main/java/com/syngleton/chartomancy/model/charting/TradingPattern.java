package com.syngleton.chartomancy.model.charting;

import lombok.Getter;

@Getter
public class TradingPattern extends Pattern {

    private final int scope;
    private final int priceVariationPrediction;

    public TradingPattern(PredictivePattern pattern) {
        super(
                pattern.getPixelatedCandles(),
                PatternType.TRADING,
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe()
        );
        this.scope = pattern.getScope();
        this.priceVariationPrediction = pattern.getPriceVariationPrediction();
    }
}

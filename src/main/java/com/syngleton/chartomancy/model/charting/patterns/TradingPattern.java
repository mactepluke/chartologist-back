package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;
import lombok.Getter;
import lombok.ToString;

import java.util.List;


public class TradingPattern extends Pattern implements PixelatedPattern {

    @ToString.Exclude
    private final List<PixelatedCandle> pixelatedCandles;
    @Getter
    private final int scope;
    @Getter
    private final int priceVariationPrediction;

    public TradingPattern(PredictivePattern pattern) {
        super(
                PatternType.TRADING,
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe()
        );
        this.pixelatedCandles = pattern.getPixelatedCandles();
        this.scope = pattern.getScope();
        this.priceVariationPrediction = pattern.getPriceVariationPrediction();
    }
    @Override
    public List<PixelatedCandle> getPixelatedCandles() {
        return pixelatedCandles;
    }
}

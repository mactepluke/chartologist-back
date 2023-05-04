package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;
import com.syngleton.chartomancy.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TradingPattern extends Pattern implements PixelatedPattern {

    @ToString.Exclude
    private final List<PixelatedCandle> pixelatedCandles;
    @Getter
    private final int scope;
    @Getter
    private final float priceVariationPrediction;

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
        this.priceVariationPrediction = Format.roundTwoDigits(pattern.getPriceVariationPrediction());
    }
    @Override
    public List<PixelatedCandle> getPixelatedCandles() {
        return pixelatedCandles;
    }
}

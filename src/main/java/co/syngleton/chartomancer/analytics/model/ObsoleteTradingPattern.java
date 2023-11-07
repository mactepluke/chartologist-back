package co.syngleton.chartomancer.analytics.model;

import co.syngleton.chartomancer.global.tools.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ObsoleteTradingPattern extends Pattern implements PixelatedPattern, ScopedPattern {

    @ToString.Exclude
    private final List<PixelatedCandle> pixelatedCandles;
    @Getter
    private final int scope;
    @Getter
    private final float priceVariationPrediction;

    public ObsoleteTradingPattern(ObsoletePredictivePattern pattern) {
        super(
                PatternType.TRADING_OBSOLETE,
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

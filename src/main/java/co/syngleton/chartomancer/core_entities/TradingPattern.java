package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class TradingPattern extends Pattern implements ScopedPattern {

    private final int scope;
    private final float priceVariationPrediction;

    public TradingPattern(PredictivePattern pattern) {
        super(
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe(),
                pattern.getIntCandles()
        );
        this.scope = pattern.getScope();
        this.priceVariationPrediction = Format.roundTwoDigits(pattern.getPriceVariationPrediction());
    }
}

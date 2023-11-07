package co.syngleton.chartomancer.analytics.model;

import co.syngleton.chartomancer.global.tools.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class TradingPattern extends Pattern implements IntPattern, ScopedPattern {

    @ToString.Exclude
    private final List<IntCandle> intCandles;
    private final int scope;
    private final float priceVariationPrediction;

    public TradingPattern(PredictivePattern pattern) {
        super(
                PatternType.TRADING,
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

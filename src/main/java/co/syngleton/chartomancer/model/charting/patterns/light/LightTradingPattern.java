package co.syngleton.chartomancer.model.charting.patterns.light;

import co.syngleton.chartomancer.model.charting.misc.PatternType;
import co.syngleton.chartomancer.model.charting.patterns.interfaces.ScopedPattern;
import co.syngleton.chartomancer.model.charting.candles.IntCandle;
import co.syngleton.chartomancer.model.charting.patterns.Pattern;
import co.syngleton.chartomancer.util.Format;
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

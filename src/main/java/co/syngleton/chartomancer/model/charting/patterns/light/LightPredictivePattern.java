package co.syngleton.chartomancer.model.charting.patterns.light;

import co.syngleton.chartomancer.model.charting.misc.PatternType;
import co.syngleton.chartomancer.model.charting.patterns.interfaces.ComputablePattern;
import co.syngleton.chartomancer.model.charting.candles.IntCandle;
import co.syngleton.chartomancer.model.charting.patterns.Pattern;
import co.syngleton.chartomancer.util.Format;
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
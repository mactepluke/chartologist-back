package co.syngleton.chartomancer.analytics.model;

import co.syngleton.chartomancer.global.tools.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class PredictivePattern extends Pattern implements ComputablePattern, IntPattern {

    @ToString.Exclude
    private final List<IntCandle> intCandles;
    private final int scope;
    private final LocalDateTime startDate;
    @Setter
    private float priceVariationPrediction = 0;

    public PredictivePattern(BasicPattern pattern, int scope) {
        super(
                PatternType.PREDICTIVE,
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
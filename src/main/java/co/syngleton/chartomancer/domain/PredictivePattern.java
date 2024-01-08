package co.syngleton.chartomancer.domain;

import co.syngleton.chartomancer.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class PredictivePattern extends Pattern implements ComputablePattern {

    private final int scope;
    private final LocalDateTime startDate;
    @Setter
    private float priceVariationPrediction = 0;

    public PredictivePattern(BasicPattern pattern, int scope) {
        super(
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe(),
                pattern.getIntCandles()
        );
        this.startDate = pattern.getStartDate();
        this.scope = Format.streamline(scope, 1, this.getLength());
    }

}
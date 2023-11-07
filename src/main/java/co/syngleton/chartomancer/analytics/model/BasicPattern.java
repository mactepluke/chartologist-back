package co.syngleton.chartomancer.analytics.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class BasicPattern extends Pattern implements IntPattern {

    private final LocalDateTime startDate;
    @ToString.Exclude
    private final List<IntCandle> intCandles;

    public BasicPattern(
            List<IntCandle> intCandles,
            int granularity,
            int length,
            Symbol symbol,
            Timeframe timeframe,
            LocalDateTime startDate
    ) {
        super(
                PatternType.BASIC,
                granularity,
                length,
                symbol,
                timeframe
        );
        this.intCandles = Collections.unmodifiableList(intCandles);
        this.startDate = startDate;
    }
}

package co.syngleton.chartomancer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class BasicPattern extends Pattern {

    private final LocalDateTime startDate;

    public BasicPattern(
            List<IntCandle> intCandles,
            int granularity,
            int length,
            Symbol symbol,
            Timeframe timeframe,
            LocalDateTime startDate
    ) {
        super(
                granularity,
                length,
                symbol,
                timeframe,
                intCandles
        );
        this.startDate = startDate;
    }
}
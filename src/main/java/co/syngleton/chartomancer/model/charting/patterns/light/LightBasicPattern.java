package co.syngleton.chartomancer.model.charting.patterns.light;

import co.syngleton.chartomancer.model.charting.misc.PatternType;
import co.syngleton.chartomancer.model.charting.misc.Symbol;
import co.syngleton.chartomancer.model.charting.misc.Timeframe;
import co.syngleton.chartomancer.model.charting.candles.IntCandle;
import co.syngleton.chartomancer.model.charting.patterns.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public final class LightBasicPattern extends Pattern implements IntPattern {

    private final LocalDateTime startDate;
    @ToString.Exclude
    private final List<IntCandle> intCandles;

    public LightBasicPattern(
            List<IntCandle> intCandles,
            int granularity,
            int length,
            Symbol symbol,
            Timeframe timeframe,
            LocalDateTime startDate
    ) {
        super(
                PatternType.LIGHT_BASIC,
                granularity,
                length,
                symbol,
                timeframe
        );
        this.intCandles = Collections.unmodifiableList(intCandles);
        this.startDate = startDate;
    }
}

package co.syngleton.chartomancer.model.charting.patterns.pixelated;

import co.syngleton.chartomancer.model.charting.misc.PatternType;
import co.syngleton.chartomancer.model.charting.misc.Symbol;
import co.syngleton.chartomancer.model.charting.misc.Timeframe;
import co.syngleton.chartomancer.model.charting.candles.PixelatedCandle;
import co.syngleton.chartomancer.model.charting.patterns.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class BasicPattern extends Pattern implements PixelatedPattern {

    @Getter
    private final LocalDateTime startDate;
    @ToString.Exclude
    private final List<PixelatedCandle> pixelatedCandles;

    public BasicPattern(
            List<PixelatedCandle> pixelatedCandles,
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
        this.pixelatedCandles = Collections.unmodifiableList(pixelatedCandles);
        this.startDate = startDate;
    }

    @Override
    public List<PixelatedCandle> getPixelatedCandles() {
        return pixelatedCandles;
    }
}

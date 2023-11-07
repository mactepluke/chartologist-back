package co.syngleton.chartomancer.analytics.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ObsoleteBasicPattern extends Pattern implements PixelatedPattern {

    @Getter
    private final LocalDateTime startDate;
    @ToString.Exclude
    private final List<PixelatedCandle> pixelatedCandles;

    public ObsoleteBasicPattern(
            List<PixelatedCandle> pixelatedCandles,
            int granularity,
            int length,
            Symbol symbol,
            Timeframe timeframe,
            LocalDateTime startDate
    ) {
        super(
                PatternType.BASIC_OBSOLETE,
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

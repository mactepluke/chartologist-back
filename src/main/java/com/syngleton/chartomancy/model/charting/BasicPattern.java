package com.syngleton.chartomancy.model.charting;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class BasicPattern extends Pattern {

    @Getter
    private final LocalDateTime startDate;

    public BasicPattern(
            List<PixelatedCandle> pixelatedCandles,
            int granularity,
            int length,
            Symbol symbol,
            Timeframe timeframe,
            LocalDateTime startDate
    ) {
        super(
                pixelatedCandles,
                PatternType.BASIC,
                granularity,
                length,
                symbol,
                timeframe
        );
        this.startDate = startDate;
    }
}

package com.syngleton.chartomancy.model;

import java.time.LocalDateTime;
import java.util.List;

public class BasicPattern extends Pattern {

    public BasicPattern(
            List<PixelatedCandle> pixelatedCandles,
            int granularity,
            int length,
            Symbol symbol,
            Timeframe timeframe,
            String name,
            LocalDateTime startDate
    ) {
        super(
                pixelatedCandles,
                PatternType.BASIC,
                granularity,
                length,
                symbol,
                timeframe,
                name,
                startDate
        );
    }
}

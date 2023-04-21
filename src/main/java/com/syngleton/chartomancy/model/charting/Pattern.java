package com.syngleton.chartomancy.model.charting;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
public abstract class Pattern extends ChartObject {
    @ToString.Exclude
    private final List<PixelatedCandle> pixelatedCandles;
    private final PatternType patternType;
    private final int granularity;
    private final int length;
    private final String name;
    private final LocalDateTime startDate;

    protected Pattern(List<PixelatedCandle> pixelatedCandles,
                      PatternType patternType,
                      int granularity,
                      int length,
                      Symbol symbol,
                      Timeframe timeframe,
                      String name,
                      LocalDateTime startDate
    ) {
        super(symbol, timeframe);
        this.pixelatedCandles = pixelatedCandles;
        this.patternType = patternType;
        this.granularity = granularity;
        this.length = length;
        this.name = name;
        this.startDate = startDate;
    }
}

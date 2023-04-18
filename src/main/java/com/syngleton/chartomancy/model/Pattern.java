package com.syngleton.chartomancy.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Data
public abstract class Pattern {
    @ToString.Exclude
    private List<PixelatedCandle> pixelatedCandles;
    private PatternType patternType;
    private int granularity;
    private int length;
    private Timeframe timeframe;
    private String name;
    private LocalDateTime startDate;
}

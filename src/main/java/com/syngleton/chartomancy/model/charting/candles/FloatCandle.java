package com.syngleton.chartomancy.model.charting.candles;

import java.time.LocalDateTime;

public record FloatCandle(LocalDateTime dateTime, float open, float high, float low, float close, float volume) {
}

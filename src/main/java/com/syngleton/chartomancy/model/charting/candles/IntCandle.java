package com.syngleton.chartomancy.model.charting.candles;

import java.time.LocalDateTime;

public record IntCandle(LocalDateTime dateTime, int open, int high, int low, int close, int volume) {
}
package com.syngleton.chartomancy.model;

import java.time.LocalDateTime;

public record Candle(LocalDateTime dateTime, float open, float high, float low, float close, float volume) {
}

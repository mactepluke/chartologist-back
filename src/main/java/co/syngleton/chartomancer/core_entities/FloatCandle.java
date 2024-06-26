package co.syngleton.chartomancer.core_entities;

import java.time.LocalDateTime;

public record FloatCandle(LocalDateTime dateTime, float open, float high, float low, float close,
                          float volume) implements Candlestick {

}

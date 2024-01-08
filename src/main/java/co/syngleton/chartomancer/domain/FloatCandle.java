package co.syngleton.chartomancer.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public record FloatCandle(LocalDateTime dateTime, float open, float high, float low, float close,
                          float volume) implements Serializable {

}

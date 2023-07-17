package co.syngleton.chartomancer.model.charting.candles;

import java.io.Serializable;
import java.time.LocalDateTime;

public record FloatCandle(LocalDateTime dateTime, float open, float high, float low, float close, float volume) implements Serializable {

}

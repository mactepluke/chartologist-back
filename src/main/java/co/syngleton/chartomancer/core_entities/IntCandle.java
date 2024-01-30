package co.syngleton.chartomancer.core_entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public record IntCandle(LocalDateTime dateTime, int open, int high, int low, int close,
                        int volume) implements Serializable {

}
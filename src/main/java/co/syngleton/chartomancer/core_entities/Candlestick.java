package co.syngleton.chartomancer.core_entities;

import java.io.Serializable;
import java.time.LocalDateTime;

interface Candlestick extends Serializable {
    LocalDateTime dateTime();
}

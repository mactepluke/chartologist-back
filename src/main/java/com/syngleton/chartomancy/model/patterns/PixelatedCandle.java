package com.syngleton.chartomancy.model.patterns;

import java.util.List;

public record PixelatedCandle(List<CandlePixel> candle, int volume) {
}

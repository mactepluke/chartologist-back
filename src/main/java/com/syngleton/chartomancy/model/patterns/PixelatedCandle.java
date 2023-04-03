package com.syngleton.chartomancy.model.patterns;

import java.util.ArrayList;

public record PixelatedCandle(ArrayList<CandlePixel> candle, short volume) {
}

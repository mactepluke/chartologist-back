package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;


public class BasicPattern extends Pattern implements PixelatedPattern {

    @Getter
    private final LocalDateTime startDate;
    @ToString.Exclude
    private final List<PixelatedCandle> pixelatedCandles;

    public BasicPattern(
            List<PixelatedCandle> pixelatedCandles,
            int granularity,
            int length,
            Symbol symbol,
            Timeframe timeframe,
            LocalDateTime startDate
    ) {
        super(
                PatternType.BASIC,
                granularity,
                length,
                symbol,
                timeframe
        );
        this.pixelatedCandles = pixelatedCandles;
        this.startDate = startDate;
    }

    @Override
    public List<PixelatedCandle> getPixelatedCandles() {
        return pixelatedCandles;
    }
}

package com.syngleton.chartomancy.factory;

import com.syngleton.chartomancy.model.charting.Candle;
import com.syngleton.chartomancy.model.charting.PixelatedCandle;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.syngleton.chartomancy.util.Format.streamlineInt;
import static java.lang.Math.round;

@Log4j2
@Component
public class CandleFactory {

    public List<PixelatedCandle> pixelateCandles(List<Candle> candles, int granularity) {
        float lowest = candles.get(0).low();
        float highest = 0;

        for (Candle candle : candles) {
            lowest = Math.min(lowest, candle.low());
            highest = Math.max(highest, candle.high());
        }

        List<PixelatedCandle> pixelatedCandles = new ArrayList<>();

        for (Candle candle : candles) {
            pixelatedCandles.add(pixelateCandle(candle, granularity, lowest, highest));
        }
        return pixelatedCandles;
    }

    private PixelatedCandle pixelateCandle(Candle candle, int granularity, float lowest, float highest)   {
        byte[] candlePixels = new byte[granularity];

        int divider = round((highest - lowest) / granularity);

        int open = round((candle.open() - lowest) / divider);
        int high = round((candle.high() - lowest) / divider);
        int low = round((candle.low() - lowest) / divider);
        int close = round((candle.close() - lowest) / divider);

        open = streamlineInt(open, 0, granularity);
        high = streamlineInt(high, 0, granularity);
        low = streamlineInt(low, 0, granularity);
        close = streamlineInt(close, 0, granularity);


        //Marking an empty pixel with 0
        for (int i = 0; i < low; i++) {
            candlePixels[i] = 0;
        }
        //Marking a wick pixel with 1
        for (int i = low; i < Math.min(open, close); i++) {
            candlePixels[i] = 1;
        }
        //Marking a body pixel with 2, an open body pixel with 3 and a close body pixel with 4
        for (int i = Math.min(open, close); i < Math.max(open, close); i++) {
            if (i == open || i == open -1)  {
                candlePixels[i] = 3;
            } else if (i == close || i == close -1)  {
                candlePixels[i] = 4;
            } else {
                candlePixels[i] = 2;
            }
        }
        //Marking a wick pixel with 1
        for (int i = Math.max(open, close); i < high; i++) {
            candlePixels[i] = 1;
        }
        //Marking an empty pixel with 0
        for (int i = high; i < granularity; i++) {
            candlePixels[i] = 0;
        }
        return new PixelatedCandle(candlePixels, (round(candle.volume() / divider)));
    }
}

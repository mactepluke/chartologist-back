package com.syngleton.chartomancy.factory;

import com.syngleton.chartomancy.model.charting.candles.FloatCandle;
import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;
import com.syngleton.chartomancy.util.Pair;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.syngleton.chartomancy.util.Format.streamline;
import static java.lang.Math.round;

@Log4j2
@Component
public class CandleFactory {

    public List<IntCandle> streamlineToIntCandles(List<FloatCandle> floatCandles, int granularity) {

        Pair<Float, Float> extremes = getLowestAndHighest(floatCandles);

        List<IntCandle> intCandles = new ArrayList<>();

        for (FloatCandle floatCandle : floatCandles) {
            intCandles.add(streamlineToIntCandle(floatCandle, granularity, extremes.first(), extremes.second()));
        }
        return intCandles;
    }

    private Pair<Float, Float> getLowestAndHighest(List<FloatCandle> floatCandles)  {

        float lowest = floatCandles.get(0).low();
        float highest = 0;

        for (FloatCandle floatCandle : floatCandles) {
            lowest = Math.min(lowest, floatCandle.low());
            highest = Math.max(highest, floatCandle.high());
        }
        return new Pair<>(lowest, highest);
    }

    private IntCandle streamlineToIntCandle(FloatCandle floatCandle, int granularity, float lowest, float highest)   {

        float divider = (highest - lowest) / granularity;

        int open = round((floatCandle.open() - lowest) / divider);
        int high = round((floatCandle.high() - lowest) / divider);
        int low = round((floatCandle.low() - lowest) / divider);
        int close = round((floatCandle.close() - lowest) / divider);
        int volume = round(floatCandle.volume() / divider);

        open = streamline(open, 0, granularity);
        high = streamline(high, 0, granularity);
        low = streamline(low, 0, granularity);
        close = streamline(close, 0, granularity);

        return new IntCandle(LocalDateTime.now(), open, high, low, close, volume);
    }

    public List<PixelatedCandle> pixelateCandles(List<FloatCandle> floatCandles, int granularity) {

        List<IntCandle> intCandles = streamlineToIntCandles(floatCandles, granularity);

        List<PixelatedCandle> pixelatedCandles = new ArrayList<>();

        for (IntCandle intCandle : intCandles) {
            pixelatedCandles.add(pixelateCandle(intCandle, granularity));
        }
        return pixelatedCandles;
    }

    private PixelatedCandle pixelateCandle(IntCandle intCandle, int granularity)   {
        byte[] candlePixels = new byte[granularity];

        //Marking an empty pixel with 0
        for (int i = 0; i < intCandle.low(); i++) {
            candlePixels[i] = 0;
        }
        //Marking a wick pixel with 1
        for (int i = intCandle.low(); i < Math.min(intCandle.open(), intCandle.close()); i++) {
            candlePixels[i] = 1;
        }
        //Marking a body pixel with 2, an open body pixel with 3 and a close body pixel with 4
        for (int i = Math.min(intCandle.open(), intCandle.close()); i < Math.max(intCandle.open(), intCandle.close()); i++) {
            if (i == intCandle.open() || i == intCandle.open() -1)  {
                candlePixels[i] = 3;
            } else if (i == intCandle.close() || i == intCandle.close() -1)  {
                candlePixels[i] = 4;
            } else {
                candlePixels[i] = 2;
            }
        }
        //Marking a wick pixel with 1
        for (int i = Math.max(intCandle.open(), intCandle.close()); i < intCandle.high(); i++) {
            candlePixels[i] = 1;
        }
        //Marking an empty pixel with 0
        for (int i = intCandle.high(); i < granularity; i++) {
            candlePixels[i] = 0;
        }
        return new PixelatedCandle(candlePixels, intCandle.volume());
    }
}

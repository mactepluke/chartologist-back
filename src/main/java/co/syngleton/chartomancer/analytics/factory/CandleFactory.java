package co.syngleton.chartomancer.analytics.factory;

import co.syngleton.chartomancer.analytics.model.FloatCandle;
import co.syngleton.chartomancer.analytics.model.IntCandle;
import co.syngleton.chartomancer.global.tools.Pair;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static co.syngleton.chartomancer.global.tools.Format.streamline;
import static java.lang.Math.round;

@Log4j2
@Component
public final class CandleFactory {

    public List<IntCandle> streamlineToIntCandles(List<FloatCandle> floatCandles, int granularity) {

        Pair<Float, Float> extremes = getLowestAndHighest(floatCandles);

        List<IntCandle> intCandles = new ArrayList<>();

        for (FloatCandle floatCandle : floatCandles) {
            intCandles.add(streamlineToIntCandle(floatCandle, granularity, extremes.first(), extremes.second()));
        }
        return intCandles;
    }

    private Pair<Float, Float> getLowestAndHighest(List<FloatCandle> floatCandles) {

        float lowest = floatCandles.get(0).low();
        float highest = 0;

        for (FloatCandle floatCandle : floatCandles) {
            lowest = Math.min(lowest, floatCandle.low());
            highest = Math.max(highest, floatCandle.high());
        }
        return new Pair<>(lowest, highest);
    }

    private IntCandle streamlineToIntCandle(FloatCandle floatCandle, int granularity, float lowest, float highest) {

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
}

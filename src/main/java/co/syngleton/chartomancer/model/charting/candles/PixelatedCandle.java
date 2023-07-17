package co.syngleton.chartomancer.model.charting.candles;


import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Log4j2
public record PixelatedCandle(byte[] candle, int volume) implements Serializable {

    @Override
    public String toString()  {
        return "PixelatedCandle{" +
                "volume=" + volume +
                ", candle=" + Arrays.toString(candle);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PixelatedCandle pixelatedCandle = (PixelatedCandle) o;
        return volume == pixelatedCandle.volume && Arrays.equals(candle, pixelatedCandle.candle);
    }

    @Override
    public int hashCode()  {
        int result = Objects.hash(volume);
        result = 31 * result + Arrays.hashCode(candle);
        return result;
    }
}

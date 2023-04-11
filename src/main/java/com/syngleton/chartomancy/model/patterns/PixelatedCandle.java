package com.syngleton.chartomancy.model.patterns;


import lombok.extern.log4j.Log4j2;

@Log4j2
public record PixelatedCandle(byte[] candle, int volume) {

    @Override
    public String toString()  {
        return "Volume : " + volume;
    }

    @Override
    public boolean equals(final Object obj) {
        log.error("equals not implemented for this object.");
        return false;
    }

    @Override
    public int hashCode()  {
        log.error("hashCode not implemented for this object.");
        return -1;
    }
}

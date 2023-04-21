package com.syngleton.chartomancy.model.charting;

public enum Timeframe {
    UNKNOWN(-1),
    SECOND(1),
    MINUTE(60),
    HALF_HOUR(1800),
    HOUR(3600),
    FOUR_HOUR(14400),
    DAY(86400),
    WEEK(604800);

    public final long durationInSeconds;

    Timeframe(long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }
}

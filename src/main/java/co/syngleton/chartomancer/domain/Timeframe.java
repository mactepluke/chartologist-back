package co.syngleton.chartomancer.model;

public enum Timeframe {
    UNKNOWN(-1, 0),
    SECOND(1, 60),
    MINUTE(60, 30),
    HALF_HOUR(1800, 2),
    HOUR(3600, 4),
    FOUR_HOUR(14400, 6),
    DAY(86400, 7),
    WEEK(604800, 4);

    public final long durationInSeconds;
    public final int scope;

    Timeframe(long durationInSeconds, int scope) {
        this.durationInSeconds = durationInSeconds;
        this.scope = scope;
    }
}

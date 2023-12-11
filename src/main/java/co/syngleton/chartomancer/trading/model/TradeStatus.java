package co.syngleton.chartomancer.trading.model;

public enum TradeStatus {
    LIMIT_ORDER,
    OPENED,
    STOP_LOSS_HIT,
    TAKE_PROFIT_HIT,
    CANCELED,
    CLOSED_MANUALLY,
    EXPIRED,
    BLANK,
    UNFUNDED
}

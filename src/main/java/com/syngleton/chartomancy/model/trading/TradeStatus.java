package com.syngleton.chartomancy.model.trading;

public enum TradeStatus {
    OPENED,
    STOP_LOSS_HIT,
    TAKE_PROFIT_HIT,
    CANCELED,
    CLOSED_MANUALLY,
    EXPIRED,
    BLANK,
    UNFUNDED
}

package com.syngleton.chartomancy.service;

public enum TradeStatus {
    OPENED,
    STOP_LOSS_HIT,
    TAKE_PROFIT_HIT,
    CANCELED,
    CLOSED_MANUALLY,
    UNFUNDED
}

package com.syngleton.chartomancy.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.syngleton.chartomancy.model.trading.Trade;
import com.syngleton.chartomancy.model.trading.TradeStatus;

import java.time.LocalDateTime;

public record TradeDTO(
        @JsonProperty boolean blank,
        @JsonProperty double size,
        @JsonProperty String side,
        @JsonProperty double openingPrice,
        @JsonProperty LocalDateTime expectedClose,
        @JsonProperty double takeProfit,
        @JsonProperty double stopLoss,
        @JsonProperty double expectedProfit,
        @JsonProperty double riskToRewardRatio,
        @JsonProperty double maxLoss,
        @JsonProperty float leverage
) {

    public TradeDTO(Trade trade) {
        this(
                trade.getStatus() == TradeStatus.BLANK,
                trade.getSize(),
                trade.isSide() ? "LONG" : "SHORT",
                trade.getOpeningPrice(),
                trade.getExpectedClose(),
                trade.getTakeProfit(),
                trade.getStopLoss(),
                trade.getExpectedProfit(),
                trade.getRewardToRiskRatio(),
                trade.getMaxLoss(),
                trade.getLeverage()
        );
    }
}

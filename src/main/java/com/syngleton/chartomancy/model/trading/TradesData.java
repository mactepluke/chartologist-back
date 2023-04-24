package com.syngleton.chartomancy.model.trading;

import java.time.LocalDateTime;

public record TradesData(
        LocalDateTime tradeOpen,
        LocalDateTime tradeClose,
        TradingStrategyRecord strategy,
        float tradeSize,
        int leverageX,
        float accountSize,
        boolean side,
        float pnl,
        String platform,
        String symbol,
        float openPrice,
        float closePrice,
        float stopLoss,
        float takeProfit,
        boolean stopLossHit,
        boolean takeProfitHit
) {
}

package co.syngleton.chartomancer.api_requesting;

import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.trading.Trade;

import java.time.LocalDateTime;

public record TradeSignalDTO(
        boolean blank,
        Timeframe timeframe,
        double size,
        String side,
        double openingPrice,
        LocalDateTime expectedClose,
        double takeProfit,
        double stopLoss,
        double expectedProfit,
        double riskToRewardRatio,
        double maxLoss,
        float leverage
) {
    public static TradeSignalDTO from(Trade trade) {
        return new TradeSignalDTO(
                trade.getStatus() == Trade.TradeStatus.BLANK,
                trade.getTimeframe(),
                trade.getSize(),
                trade.isSideLong() ? "LONG" : "SHORT",
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

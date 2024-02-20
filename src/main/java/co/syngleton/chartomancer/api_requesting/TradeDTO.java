package co.syngleton.chartomancer.api_requesting;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.trading.Trade;

import java.time.LocalDateTime;

public record TradeDTO(
        LocalDateTime openDateTime,
        LocalDateTime lastUpdate,
        String platform,
        Symbol symbol,
        Timeframe timeframe,
        double accountBalanceAtOpen,
        double size,
        LocalDateTime expectedClose,
        LocalDateTime expiry,
        LocalDateTime closeDateTime,
        Trade.TradeStatus status,
        String side,
        double openPrice,
        double closePrice,
        double takeProfit,
        double stopLoss,
        double leverage,
        double takeProfitPricePercentage,
        double stopLossPricePercentage,
        double expectedProfit,
        double rewardToRiskRatio,
        double pnl,
        double feePercentage,
        double feeAmount
) {
    public static TradeDTO from(Trade trade) {
        return new TradeDTO(
                trade.getOpenDateTime(),
                trade.getLastUpdate(),
                trade.getPlatform(),
                trade.getSymbol(),
                trade.getTimeframe(),
                trade.getAccountBalanceAtOpen(),
                trade.getSize(),
                trade.getExpectedClose(),
                trade.getExpiry(),
                trade.getCloseDateTime(),
                trade.getStatus(),
                trade.isSideLong() ? "long" : "short",
                trade.getOpeningPrice(),
                trade.getClosingPrice(),
                trade.getTakeProfit(),
                trade.getStopLoss(),
                trade.getLeverage(),
                trade.getTakeProfitPricePercentage(),
                trade.getStopLossPricePercentage(),
                trade.getExpectedProfit(),
                trade.getRewardToRiskRatio(),
                trade.getPnL(),
                trade.getFeePercentage(),
                trade.getFeeAmount()
        );
    }
}

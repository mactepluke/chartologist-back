package co.syngleton.chartomancer.api_requesting;

import co.syngleton.chartomancer.trading.TradingSimulationResult;

import java.util.List;

public record BacktestingResultsBasicDTO(
        double accountBalance,
        double pnl,
        double returnPercentage,
        double annualizedReturnPercentage,
        long tradeNumber,
        double battingAveragePercentage,
        double winLossRatio,
        double profitFactor,
        double totalDurationInDays,
        double actualTradingDurationPercentage,
        double feePercentage,
        List<TradeDTO> trades
) {

    public static BacktestingResultsBasicDTO from(TradingSimulationResult result) {
        return new BacktestingResultsBasicDTO(
                result.account().getBalance(),
                result.account().getTotalPnl(),
                result.account().getTotalReturnPercentage(),
                result.annualizedReturnPercentage(),
                result.account().getNumberOfTrades(),
                result.account().getTotalBattingAveragePercentage(),
                result.account().getTotalWinToLossRatio(),
                result.account().getProfitFactor(),
                result.totalDurationInDays(),
                result.usefulToUselessTradesRatio() * 100,
                result.account().getAverageFeePercentage(),
                List.copyOf(result.account().exportTrades().stream().map(TradeDTO::from).toList())
        );
    }
}

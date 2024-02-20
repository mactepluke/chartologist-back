package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.util.Calc;
import co.syngleton.chartomancer.util.Format;
import lombok.NonNull;

public record TradingSimulationDefaultResult(
        TradingAccount account,
        double initialAccountBalance,
        Symbol symbol,
        Timeframe timeframe,
        int blankTradeCount,
        double totalDurationInDays,
        double annualizedReturnPercentage,
        double usefulToUselessTradesRatio
) implements TradingSimulationResult {

    private static final int DAYS_IN_A_YEAR = 356;

    public static @NonNull TradingSimulationDefaultResult generateFrom(@NonNull TradingAccount account,
                                                                       double initialAccountBalance,
                                                                       Symbol symbol,
                                                                       @NonNull Timeframe timeframe,
                                                                       int blankTradeCount) {

        final double totalDurationInSeconds = account.getTotalTradeDurationsInSeconds() + (double) blankTradeCount * timeframe.durationInSeconds;

        final double totalDurationInDays = Format.roundTwoDigits(totalDurationInSeconds / Timeframe.DAY.durationInSeconds);

        final double annualizedReturn = Format.roundTwoDigits(
                (DAYS_IN_A_YEAR * Timeframe.DAY.durationInSeconds
                        * Calc.relativePercentage(account.getTotalPnl(), initialAccountBalance))
                        / totalDurationInSeconds);

        final double usefulToUselessTradesRatio = blankTradeCount == 0 ? -1 :
                Format.roundTwoDigits((account.getNumberOfLongs() + account.getNumberOfShorts()) / (float) blankTradeCount);

        return new TradingSimulationDefaultResult(account,
                initialAccountBalance,
                symbol,
                timeframe,
                blankTradeCount,
                totalDurationInDays,
                annualizedReturn,
                usefulToUselessTradesRatio);
    }
}

package co.syngleton.chartomancer.trading;

public record TradingSimulationResult(
        TradingAccount account,
        int blankTradeCount
) {
}

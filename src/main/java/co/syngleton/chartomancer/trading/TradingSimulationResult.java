package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;

public interface TradingSimulationResult {

    TradingAccount account();

    double initialAccountBalance();

    Symbol symbol();

    Timeframe timeframe();

    int blankTradeCount();

    double totalDurationInDays();

    double annualizedReturnPercentage();

    double usefulToUselessTradesRatio();

}

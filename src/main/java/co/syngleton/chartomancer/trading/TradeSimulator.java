package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.core_entities.Account;
import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.Graph;
import lombok.NonNull;

public interface TradeSimulator extends TradeGenerator {

    TradingSimulationResult simulateTrades(@NonNull final TradeSimulationStrategy strat, final TradingConditionsChecker checker);

    @Override
    Trade generateOptimalTakerTrade(Account tradingAccount,
                                    Graph graph,
                                    CoreData coreData,
                                    int tradeOpenCandle);

    @Override
    Analyzer getTradingAnalyzer();

    @Override
    TradingProperties getTradingProperties();
}

package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.core_entities.Account;
import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.Graph;

public interface TradeGenerator {
    Trade generateOptimalTradeWithDefaultSettings(Account tradingAccount,
                                                  Graph graph,
                                                  CoreData coreData,
                                                  int tradeOpenCandle);

    Trade generateOptimalTrade(Account tradingAccount,
                               Graph graph,
                               CoreData coreData,
                               int tradeOpenCandle);

    Analyzer getTradingAnalyzer();

    TradingProperties getTradingProperties();

}

package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.core_entities.Account;
import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.Graph;

public interface TradeGenerator {
    Trade generateOptimalTakerTradeWithDefaultSettings(Account tradingAccount,
                                                       Graph graph,
                                                       CoreData coreData,
                                                       int tradeOpenCandle);

    Trade generateOptimalTakerTrade(Account tradingAccount,
                                    Graph graph,
                                    CoreData coreData,
                                    int tradeOpenCandle);

    Analyzer getTradingAnalyzer();

    TradingProperties getTradingProperties();

}

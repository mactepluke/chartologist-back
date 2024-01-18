package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.shared_domain.Account;
import co.syngleton.chartomancer.shared_domain.CoreData;
import co.syngleton.chartomancer.shared_domain.Graph;

public interface TradeGenerator {
    Trade generateOptimalTradeWithDefaultSettings(Account tradingAccount,
                                                  Graph graph,
                                                  CoreData coreData,
                                                  int tradeOpenCandle);

    Trade generateOptimalTrade(Account tradingAccount,
                               Graph graph,
                               CoreData coreData,
                               int tradeOpenCandle,
                               TradingSettings settings);

    Analyzer getAnalyzer();

    TradingSettings getTradingSettings();
}

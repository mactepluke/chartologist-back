package co.syngleton.chartomancer.api_requesting;

import co.syngleton.chartomancer.core_entities.Account;
import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.Graph;
import co.syngleton.chartomancer.trading.Trade;

public interface RequestingTradingProvider {

    Trade generateOptimalTakerTrade(Account tradingAccount,
                                    Graph graph,
                                    CoreData coreData,
                                    int tradeOpenCandle);
}

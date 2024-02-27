package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.api_requesting.RequestingTradingProvider;
import co.syngleton.chartomancer.automation.AutomationTradingProvider;
import co.syngleton.chartomancer.core_entities.Account;
import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.CoreDataSettingNames;
import co.syngleton.chartomancer.core_entities.Graph;

import java.util.Map;

public interface TradingService extends RequestingTradingProvider, AutomationTradingProvider {

    /**
     * This method finds the best price variation percentages and uses it to set the take profit;
     * then it sets the stop loss as being under or above the price by a percentage that equals that of the
     * price variation, divided by the REWARD_TO_RISK ratio. Then it calculates the size based on the account balance
     * and the RISK_percentage, and adjusts the leverage accordingly.
     *
     * @param tradingAccount
     * @param graph
     * @param coreData
     * @param tradeOpenCandle
     * @return
     */
    @Override
    Trade generateOptimalTakerTrade(Account tradingAccount, Graph graph, CoreData coreData, int tradeOpenCandle);

    @Override
    TradingSimulationResult simulateTrades(TradeSimulationStrategy strat, TradingConditionsChecker checker);

    @Override
    TradingProperties getTradingProperties();

    @Override
    Map<CoreDataSettingNames, String> getAnalyzerSettingsSnapshot();

}

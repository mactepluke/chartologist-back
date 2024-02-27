package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.core_entities.CoreDataSettingNames;
import co.syngleton.chartomancer.trading.TradeSimulationStrategy;
import co.syngleton.chartomancer.trading.TradingConditionsChecker;
import co.syngleton.chartomancer.trading.TradingProperties;
import co.syngleton.chartomancer.trading.TradingSimulationResult;
import lombok.NonNull;

import java.util.Map;

public interface AutomationTradingProvider {

    TradingSimulationResult simulateTrades(@NonNull final TradeSimulationStrategy strat, final TradingConditionsChecker checker);

    TradingProperties getTradingProperties();

    Map<CoreDataSettingNames, String> getAnalyzerSettingsSnapshot();
}

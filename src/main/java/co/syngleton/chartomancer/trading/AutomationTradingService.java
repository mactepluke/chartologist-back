package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.core_entities.CoreDataSettingNames;
import lombok.NonNull;

import java.util.Map;

public interface AutomationTradingService {

    TradingSimulationResult simulateTrades(@NonNull final TradeSimulationStrategy strat, final TradingConditionsChecker checker);

    TradingProperties getTradingProperties();

    Map<CoreDataSettingNames, String> getAnalyzerSettingsSnapshot();
}

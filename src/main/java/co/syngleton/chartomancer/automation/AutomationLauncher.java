package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.analytics.PatternComputingService;
import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.domain.CoreData;
import co.syngleton.chartomancer.trading.TradingService;

public interface AutomationLauncher {
    void launchAutomation(CoreData coreData,
                          DataProcessor dataProcessor,
                          PatternComputingService patternComputingService,
                          TradingService tradingService);
}

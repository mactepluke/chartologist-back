package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.domain.CoreData;
import co.syngleton.chartomancer.pattern_recognition.PatternComputer;
import co.syngleton.chartomancer.trading.TradingService;

public interface AutomationLauncher {
    void launchAutomation(CoreData coreData,
                          DataProcessor dataProcessor,
                          PatternComputer patternComputer,
                          TradingService tradingService);
}

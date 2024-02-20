package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.pattern_recognition.PatternComputer;
import co.syngleton.chartomancer.trading.TradeSimulator;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
@AllArgsConstructor
class AutomationConfig {
    private final CoreData coreData;
    private final TradeSimulator tradeSimulator;
    private final DataProcessor dataProcessor;
    private final PatternComputer patternComputer;
    private final AutomationProperties automationProperties;

    @PostConstruct
    void launchAutomation() {
        if (automationProperties.launchAutomation()) {
            log.info("Launching automation...");

            Automation automation = new Automation(
                    coreData,
                    dataProcessor,
                    patternComputer,
                    tradeSimulator,
                    automationProperties.printCoreData(),
                    automationProperties.printPricePredictionSummary(),
                    automationProperties.runBasicDummyTrades(),
                    automationProperties.runRandomizedDummyTrades(),
                    automationProperties.runRandomizedDummyTradesOnDummyGraphs(),
                    automationProperties.runDeterministicDummyTradesOnDummyGraphs(),
                    automationProperties.dummyTradesInitialBalance(),
                    automationProperties.dummyTradesMinimumBalance(),
                    automationProperties.dummyTradesExpectedBalanceX(),
                    automationProperties.dummyTradesMaxTrades(),
                    automationProperties.dummyTradesTimeframes(),
                    automationProperties.writeDummyTradesReports(),
                    automationProperties.dummyGraphsDataFolderName(),
                    automationProperties.dummyGraphsDataFilesNames(),
                    automationProperties.printTasksHistory());

            automation.launch();

        }
    }
}

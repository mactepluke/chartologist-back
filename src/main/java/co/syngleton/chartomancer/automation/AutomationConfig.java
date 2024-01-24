package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.pattern_recognition.PatternComputer;
import co.syngleton.chartomancer.shared_domain.CoreData;
import co.syngleton.chartomancer.trading.TradeGenerator;
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
    private final TradeGenerator tradeGenerator;
    private final TradeSimulator tradeSimulator;
    private final DataProcessor dataProcessor;
    private final PatternComputer patternComputer;
    private final AutomationProperties automationProperties;

    @PostConstruct
    void launchAutomation() {
        if (automationProperties.isLaunchAutomation()) {
            log.info("Launching automation...");

            Thread automation = new Thread(new Automation(
                    coreData,
                    dataProcessor,
                    patternComputer,
                    tradeGenerator,
                    tradeSimulator,
                    automationProperties.isPrintCoreData(),
                    automationProperties.isPrintPricePredictionSummary(),
                    automationProperties.isRunBasicDummyTrades(),
                    automationProperties.isRunRandomizedDummyTrades(),
                    automationProperties.isRunRandomizedDummyTradesOnDummyGraphs(),
                    automationProperties.isRunDeterministicDummyTradesOnDummyGraphs(),
                    automationProperties.getDummyTradesInitialBalance(),
                    automationProperties.getDummyTradesMinimumBalance(),
                    automationProperties.getDummyTradesExpectedBalanceX(),
                    automationProperties.getDummyTradesMaxTrades(),
                    automationProperties.getDummyTradesTimeframes(),
                    automationProperties.isWriteDummyTradesReports(),
                    automationProperties.getDummyGraphsDataFolderName(),
                    automationProperties.getDummyGraphsDataFilesNames(),
                    automationProperties.isPrintTasksHistory()));
            automation.start();
        }
    }
}

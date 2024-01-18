package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.pattern_recognition.PatternComputer;
import co.syngleton.chartomancer.shared_domain.CoreData;
import co.syngleton.chartomancer.trading.TradeGenerator;
import co.syngleton.chartomancer.trading.TradeSimulator;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
@Log4j2
class AutomationConfig {
    private final CoreData coreData;
    private final TradeGenerator tradeGenerator;
    private final TradeSimulator tradeSimulator;
    private final DataProcessor dataProcessor;
    private final PatternComputer patternComputer;
    @Value("${print_core_data:false}")
    private boolean printCoreData;
    @Value("${print_price_prediction_summary:false}")
    private boolean printPricePredictionSummary;
    @Value("${run_basic_dummy_trades:false}")
    private boolean runBasicDummyTrades;
    @Value("${run_randomized_dummy_trades:false}")
    private boolean runRandomizedDummyTrades;
    @Value("${run_randomized_dummy_trades_on_dummy_graphs:false}")
    private boolean runRandomizedDummyTradesOnDummyGraphs;
    @Value("${run_deterministic_dummy_trades_on_dummy_graphs:false}")
    private boolean runDeterministicDummyTradesOnDummyGraphs;
    @Value("${dummy_graphs_data_folder_name:dummy_data}")
    private String dummyGraphsDataFolderName;
    @Value("#{'${dummy_graphs_data_files_names}'.split(',')}")
    private List<String> dummyGraphsDataFilesNames;
    @Value("${dummy_trades_initial_balance:100000}")
    private double initialBalance;
    @Value("${dummy_trades_minimum_balance:50000}")
    private double minimumBalance;
    @Value("${dummy_trades_expected_balance_X:2}")
    private int expectedBalanceX;
    @Value("${dummy_trades_max_trades:1000}")
    private int maxTrades;
    @Value("#{'${dummy_trades_timeframes}'.split(',')}")
    private Set<Timeframe> dummyTradesTimeframes;
    @Value("${write_dummy_trades_reports:false}")
    private boolean writeDummyTradeReports;
    @Value("${print_tasks_history:false}")
    private boolean printTasksHistory;
    @Value("${launch_automation:false}")
    private boolean launchAutomation;

    @Autowired
    AutomationConfig(CoreData coreData,
                     TradeGenerator tradeGenerator,
                     TradeSimulator tradeSimulator,
                     DataProcessor dataProcessor,
                     PatternComputer patternComputer) {
        this.coreData = coreData;
        this.tradeGenerator = tradeGenerator;
        this.tradeSimulator = tradeSimulator;
        this.dataProcessor = dataProcessor;
        this.patternComputer = patternComputer;
    }

    @PostConstruct
    void launchAutomation() {
        if (launchAutomation) {
            log.info("Launching automation...");

            Thread automation = new Thread(new Automation(
                    coreData,
                    dataProcessor,
                    patternComputer,
                    tradeGenerator,
                    tradeSimulator,
                    printCoreData,
                    printPricePredictionSummary,
                    runBasicDummyTrades,
                    runRandomizedDummyTrades,
                    runRandomizedDummyTradesOnDummyGraphs,
                    runDeterministicDummyTradesOnDummyGraphs,
                    initialBalance,
                    minimumBalance,
                    expectedBalanceX,
                    maxTrades,
                    dummyTradesTimeframes,
                    writeDummyTradeReports,
                    dummyGraphsDataFolderName,
                    dummyGraphsDataFilesNames,
                    printTasksHistory));
            automation.start();
        }
    }
}

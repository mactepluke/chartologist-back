package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.analytics.PatternComputingService;
import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.domain.CoreData;
import co.syngleton.chartomancer.domain.Timeframe;
import co.syngleton.chartomancer.trading.TradingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
final class AutomationService implements AutomationLauncher {

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

    @Override
    public void launchAutomation(CoreData coreData,
                                 DataProcessor dataProcessor,
                                 PatternComputingService patternComputingService,
                                 TradingService tradingService) {
        Thread automation = new Thread(new Automation(
                coreData,
                dataProcessor,
                patternComputingService,
                tradingService,
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

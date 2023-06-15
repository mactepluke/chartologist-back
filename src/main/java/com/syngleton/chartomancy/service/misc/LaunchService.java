package com.syngleton.chartomancy.service.misc;

import com.syngleton.chartomancy.automation.Automation;
import com.syngleton.chartomancy.controller.devtools.DataController;
import com.syngleton.chartomancy.controller.devtools.PatternController;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.service.domain.DataService;
import com.syngleton.chartomancy.service.domain.PatternService;
import com.syngleton.chartomancy.service.domain.TradingService;
import com.syngleton.chartomancy.view.InteractiveShell;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LaunchService {

    @Value("${print_core_data:false}")
    private boolean printCoreData;
    @Value("${print_price_prediction_summary:false}")
    private boolean printPricePredictionSummary;
    @Value("${run_basic_dummy_trades:false}")
    private boolean runBasicDummyTrades;
    @Value("${run_advanced_dummy_trades:false}")
    private boolean runAdvancedDummyTrades;
    @Value("${dummy_trades_initial_balance:100000}")
    private double initialBalance;
    @Value("${dummy_trades_minimum_balance:50000}")
    private double minimumBalance;
    @Value("${dummy_trades_expected_balance_X:2}")
    private int expectedBalanceX;
    @Value("${dummy_trades_max_trades:1000}")
    private int maxTrades;
    @Value("${write_dummy_trades_reports:false}")
    private boolean writeDummyTradeReports;
    @Value("${print_tasks_history:false}")
    private boolean printTasksHistory;

    public boolean launchShell(DataController dataController,
                               PatternController patternController,
                               CoreData coreData) {
        Thread interactiveShell = new Thread(new InteractiveShell(dataController, patternController, coreData));
        interactiveShell.start();
        return true;
    }

    public void launchAutomation(CoreData coreData,
                                 DataService dataService,
                                 PatternService patternService,
                                 TradingService tradingService) {
        Thread automation = new Thread(new Automation(
                coreData,
                dataService,
                patternService,
                tradingService,
                printCoreData,
                printPricePredictionSummary,
                runBasicDummyTrades,
                runAdvancedDummyTrades,
                initialBalance,
                minimumBalance,
                expectedBalanceX,
                maxTrades,
                writeDummyTradeReports,
                printTasksHistory));
        automation.start();
    }

}

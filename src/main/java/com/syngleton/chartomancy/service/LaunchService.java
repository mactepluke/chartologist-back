package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.analytics.Automation;
import com.syngleton.chartomancy.controller.root.DataController;
import com.syngleton.chartomancy.controller.root.PatternController;
import com.syngleton.chartomancy.data.CoreData;
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
    @Value("${print_tasks_history:false}")
    private boolean printTasksHistory;

    public boolean launchShell(DataController dataController,
                               PatternController patternController,
                               CoreData coreData)   {
        Thread interactiveShell = new Thread(new InteractiveShell(dataController, patternController, coreData));
        interactiveShell.start();
        return true;
    }

    public void launchAutomation(CoreData coreData,
                                    DataService dataService,
                                    PatternService patternService,
                                    TradingService tradingService)   {
        Thread automation = new Thread(new Automation(
                coreData,
                dataService,
                patternService,
                tradingService,
                printCoreData,
                printPricePredictionSummary,
                runBasicDummyTrades,
                runAdvancedDummyTrades,
                printTasksHistory));
        automation.start();
    }

}

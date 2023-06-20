package com.syngleton.chartomancy.automation;

import com.syngleton.chartomancy.automation.dummytrades.DummyTradesManager;
import com.syngleton.chartomancy.automation.dummytrades.DummyTradesSummaryTable;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.PatternBox;
import com.syngleton.chartomancy.model.charting.patterns.interfaces.ScopedPattern;
import com.syngleton.chartomancy.service.domain.DataService;
import com.syngleton.chartomancy.service.domain.PatternService;
import com.syngleton.chartomancy.service.domain.TradingService;
import com.syngleton.chartomancy.util.Check;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Log4j2
public class Automation implements Runnable {
    private static final String DUMMY_TRADES_FOLDER_PATH = "./trades_history/";
    private static final String DUMMY_TRADES_SUMMARY_FILE_NAME = "dummy_trades_summary";
    private static final String NEW_LINE = System.getProperty("line.separator");
    private final boolean printCoreData;
    private final boolean printPricePredictionSummary;
    private final boolean runBasicDummyTrades;
    private final boolean runRandomizedDummyTrades;
    private final boolean runRandomizedDummyTradesOnDummyGraphs;
    private final boolean runDeterministicDummyTradesOnDummyGraphs;
    private final boolean writeDummyTradeReports;
    private final String dummyGraphsDataFolderName;
    private final List<String> dummyGraphsDataFilesNames;
    private final boolean printTasksHistory;
    private final List<String> tasksHistory;
    private final CoreData coreData;
    private final DataService dataService;
    private final PatternService patternService;
    private final DummyTradesManager dtm;
    private final DummyTradesSummaryTable dummyTradesSummaryTable;
    private String reportLog;

    public Automation(CoreData coreData,
                      DataService dataService,
                      PatternService patternService,
                      TradingService tradingService,
                      boolean printCoreData,
                      boolean printPricePredictionSummary,
                      boolean runBasicDummyTrades,
                      boolean runRandomizedDummyTrades,
                      boolean runRandomizedDummyTradesOnDummyGraphs,
                      boolean runDeterministicDummyTradesOnDummyGraphs,
                      double initialBalance,
                      double minimumBalance,
                      int expectedBalanceX,
                      int maxTrades,
                      boolean writeDummyTradeReports,
                      String dummyGraphsDataFolderName,
                      List<String> dummyGraphsDataFilesNames,
                      boolean printTasksHistory) {
        this.coreData = coreData;
        this.dataService = dataService;
        this.patternService = patternService;
        this.printCoreData = printCoreData;
        this.printPricePredictionSummary = printPricePredictionSummary;
        this.runBasicDummyTrades = runBasicDummyTrades;
        this.runRandomizedDummyTrades = runRandomizedDummyTrades;
        this.runRandomizedDummyTradesOnDummyGraphs = runRandomizedDummyTradesOnDummyGraphs;
        this.runDeterministicDummyTradesOnDummyGraphs = runDeterministicDummyTradesOnDummyGraphs;
        this.writeDummyTradeReports = writeDummyTradeReports;
        this.dummyGraphsDataFolderName = dummyGraphsDataFolderName;
        this.dummyGraphsDataFilesNames = dummyGraphsDataFilesNames;
        this.printTasksHistory = printTasksHistory;
        this.reportLog = "";

        dummyTradesSummaryTable = new DummyTradesSummaryTable(DUMMY_TRADES_SUMMARY_FILE_NAME);

        dtm = new DummyTradesManager(initialBalance,
                minimumBalance,
                expectedBalanceX,
                maxTrades,
                tradingService,
                coreData,
                writeDummyTradeReports,
                dummyTradesSummaryTable,
                DUMMY_TRADES_FOLDER_PATH,
                dataService);
        tasksHistory = new ArrayList<>();
    }

    @Override
    public void run() {
        log.info("*** AUTOMATION LAUNCHED ***");

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        log.debug("printCoreData: {}," + NEW_LINE
                        + "printPricePredictionSummary: {}," + NEW_LINE
                        + "runBasicDummyTrades: {}," + NEW_LINE
                        + "runAdvancedDummyTrades: {}," + NEW_LINE
                        + "runAdvancedDummyTradesOnDummyGraphs: {}," + NEW_LINE
                        + "runDeterministicDummyTradesOnDummyGraphs: {}," + NEW_LINE
                        + "writeDummyTradesReports: {}," + NEW_LINE
                        + "printTasksHistory: {}," + NEW_LINE,
                printCoreData,
                printPricePredictionSummary,
                runBasicDummyTrades,
                runRandomizedDummyTrades,
                runRandomizedDummyTradesOnDummyGraphs,
                runDeterministicDummyTradesOnDummyGraphs,
                writeDummyTradeReports,
                printTasksHistory
        );

        if (printCoreData) {
            printCoreData();
            tasksHistory.add("PRINTED CORE DATA");
        }
        if (printPricePredictionSummary) {
            printPricePredictionSummary();
            tasksHistory.add("PRINTED PRICE PREDICTION SUMMARY");
        }
        if (runBasicDummyTrades) {
            runBasicDummyTrades();
            tasksHistory.add("RAN BASIC DUMMY TRADES");
        }
        if (runRandomizedDummyTrades) {
            runRandomizedDummyTrades();
            tasksHistory.add("RAN RANDOMIZED DUMMY TRADES");
        }
        if (runRandomizedDummyTradesOnDummyGraphs) {
            runRandomizedDummyTradesOnDummyGraphs();
            tasksHistory.add("RAN RANDOMIZED DUMMY TRADES ON DUMMY GRAPHS");
        }
        if (runDeterministicDummyTradesOnDummyGraphs) {
            runDeterministicDummyTradesOnDummyGraphs();
            tasksHistory.add("RAN DETERMINISTIC DUMMY TRADES ON DUMMY GRAPHS");
        }
        if (writeDummyTradeReports) {
            writeDummyTradesReports();
            tasksHistory.add("WROTE DUMMY TRADES REPORTS");
        }
        if (printTasksHistory) {
            printTasksHistory();
        }

        stopWatch.stop();

        log.info("Automation time: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getLastTaskTimeMillis()));
    }

    private void printTasksHistory() {

        StringBuilder tasks = new StringBuilder();

        for (String task : tasksHistory) {
            tasks.append("> ").append(task).append(NEW_LINE);
        }

        log.info("*** Automation Tasks History ***" + NEW_LINE + "{}", tasks.toString());
    }

    private void printCoreData() {
        dataService.printCoreData(coreData);
    }

    private void printPricePredictionSummary() {

        long positivePricePredictions;
        long negativePricePredictions;
        long zeroPricePredictions;
        double minPricePrediction;
        double maxPricePrediction;
        double totalPriceVariation;

        Set<PatternBox> availablePatternBoxes = null;

        if (coreData != null) {
            if (coreData.getPatternBoxes() != null) {
                availablePatternBoxes = coreData.getPatternBoxes();
            }
            if (coreData.getTradingPatternBoxes() != null) {
                availablePatternBoxes = coreData.getTradingPatternBoxes();
            }
        }

        if (coreData != null && availablePatternBoxes != null) {

            for (PatternBox patternBox : availablePatternBoxes) {

                positivePricePredictions = patternBox.getListOfAllPatterns().stream()
                        .filter(pattern -> ((ScopedPattern) pattern).getPriceVariationPrediction() > 0)
                        .count();

                negativePricePredictions = patternBox.getListOfAllPatterns().stream()
                        .filter(pattern -> ((ScopedPattern) pattern).getPriceVariationPrediction() < 0)
                        .count();

                zeroPricePredictions = patternBox.getListOfAllPatterns().stream()
                        .filter(pattern -> ((ScopedPattern) pattern).getPriceVariationPrediction() == 0)
                        .count();

                totalPriceVariation = patternBox.getListOfAllPatterns().stream()
                        .mapToDouble(pattern -> ((ScopedPattern) pattern).getPriceVariationPrediction())
                        .sum();

                minPricePrediction = patternBox.getListOfAllPatterns().stream()
                        .mapToDouble(pattern -> ((ScopedPattern) pattern).getPriceVariationPrediction())
                        .min().orElse(0);

                maxPricePrediction = patternBox.getListOfAllPatterns().stream()
                        .mapToDouble(pattern -> ((ScopedPattern) pattern).getPriceVariationPrediction())
                        .max().orElse(0);

                log.info(NEW_LINE
                                + "PATTERN_BOX ({}, {})" + NEW_LINE
                                + "Positive price predictions: {}" + NEW_LINE
                                + "Negative price predictions: {}" + NEW_LINE
                                + "Zero-valued price predictions: {}" + NEW_LINE
                                + "Minimum price predictions: {}" + NEW_LINE
                                + "Maximum price predictions: {}" + NEW_LINE
                                + "AVERAGE PRICE PREDICTION: {}" + NEW_LINE,
                        patternBox.getSymbol(), patternBox.getTimeframe(),
                        positivePricePredictions,
                        negativePricePredictions,
                        zeroPricePredictions,
                        minPricePrediction,
                        maxPricePrediction,
                        totalPriceVariation / patternBox.getListOfAllPatterns().size());
            }
        } else {
            log.error("Cannot print price prediction summary: core data is missing.");
        }
    }


    public void runBasicDummyTrades() {
        if (coreData == null
                || !Check.notNullNotEmpty(coreData.getGraphs())
                || !Check.notNullNotEmpty(coreData.getTradingPatternBoxes())) {
            log.error("Could not run dummy trades: core data are missing.");
        } else {

            Optional<Graph> graph = coreData.getGraphs().stream().findAny();

            graph.ifPresent(value -> reportLog = dtm.launchDummyTrades(value, coreData, true, reportLog));
        }
    }

    private void runRandomizedDummyTrades() {
        runRandomizedDummyTrades(coreData);
    }

    private void runRandomizedDummyTrades(CoreData coreData) {
        if (coreData == null
                || !Check.notNullNotEmpty(coreData.getGraphs())
                || !Check.notNullNotEmpty(coreData.getTradingPatternBoxes())) {
            log.error("Could not run dummy trades: core data are missing.");
        } else {

            ProgressBar pb = new ProgressBar("Processing trades...", coreData.getGraphs().size());

            pb.start();

            for (Graph graph : coreData.getGraphs()) {
                reportLog = dtm.launchDummyTrades(graph, coreData, true, reportLog);

                pb.step();
            }
            pb.stop();
        }
    }

    private void runRandomizedDummyTradesOnDummyGraphs() {
        CoreData dummyGraphsData = new CoreData();

        dummyGraphsData.copy(coreData);
        dummyGraphsData.purgeNonTrading();
        dataService.loadGraphs(dummyGraphsData, dummyGraphsDataFolderName, dummyGraphsDataFilesNames);
        dataService.createGraphsForMissingTimeframes(dummyGraphsData);

        runRandomizedDummyTrades(dummyGraphsData);
    }

    private void runDeterministicDummyTradesOnDummyGraphs() {


    }

    private synchronized void writeDummyTradesReports() {
        log.info(reportLog);
        dataService.writeCsvFile(DUMMY_TRADES_FOLDER_PATH + DUMMY_TRADES_SUMMARY_FILE_NAME, dummyTradesSummaryTable);
        reportLog = "";
    }

}
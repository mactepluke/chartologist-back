package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.*;
import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.pattern_recognition.PatternComputer;
import co.syngleton.chartomancer.trading.TradeGenerator;
import co.syngleton.chartomancer.trading.TradeSimulator;
import co.syngleton.chartomancer.util.csvwritertool.CSVWriter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Log4j2
final class Automation implements Runnable {
    private static final String NEW_LINE = System.lineSeparator();
    private static final String DUMMY_TRADES_FOLDER_PATH = "./trades_history/";
    private static final String DUMMY_TRADES_SUMMARY_FILE_NAME = "dummy_trades_summary";
    private static final String DATA_MISSING_ERROR = "Could not run dummy trades: core data are missing.";
    private final boolean printCoreData;
    private final boolean printPricePredictionSummary;
    private final boolean runBasicDummyTrades;
    private final boolean runRandomizedDummyTrades;
    private final boolean runRandomizedDummyTradesOnDummyGraphs;
    private final boolean runDeterministicDummyTradesOnDummyGraphs;
    private final boolean writeDummyTradeReports;
    private final String dummyGraphsDataFolderName;
    private final List<String> dummyGraphsDataFilesNames;
    private final Set<Timeframe> dummyTradesTimeframes;
    private final boolean printTasksHistory;
    private final List<String> tasksHistory;
    private final CoreData coreData;
    private final DataProcessor dataProcessor;
    private final PatternComputer patternComputer;
    private final DummyTradesManager dtm;
    private final DummyTradesSummaryTable dummyTradesSummaryTable;
    private String reportLog;

    public Automation(CoreData coreData,
                      DataProcessor dataProcessor,
                      PatternComputer patternComputer,
                      TradeGenerator tradeGenerator,
                      TradeSimulator tradeSimulator,
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
                      Set<Timeframe> dummyTradesTimeframes,
                      boolean writeDummyTradesReports,
                      String dummyGraphsDataFolderName,
                      List<String> dummyGraphsDataFilesNames,
                      boolean printTasksHistory) {
        this.coreData = coreData;
        this.dataProcessor = dataProcessor;
        this.patternComputer = patternComputer;
        this.printCoreData = printCoreData;
        this.printPricePredictionSummary = printPricePredictionSummary;
        this.runBasicDummyTrades = runBasicDummyTrades;
        this.runRandomizedDummyTrades = runRandomizedDummyTrades;
        this.runRandomizedDummyTradesOnDummyGraphs = runRandomizedDummyTradesOnDummyGraphs;
        this.runDeterministicDummyTradesOnDummyGraphs = runDeterministicDummyTradesOnDummyGraphs;
        this.writeDummyTradeReports = writeDummyTradesReports;
        this.dummyGraphsDataFolderName = dummyGraphsDataFolderName;
        this.dummyGraphsDataFilesNames = dummyGraphsDataFilesNames;

        if (dummyTradesTimeframes == null || dummyTradesTimeframes.isEmpty()) {
            dummyTradesTimeframes = new HashSet<>(List.of(
                    Timeframe.SECOND,
                    Timeframe.MINUTE,
                    Timeframe.HALF_HOUR,
                    Timeframe.HOUR,
                    Timeframe.FOUR_HOUR,
                    Timeframe.DAY,
                    Timeframe.WEEK
            ));
        }

        this.dummyTradesTimeframes = dummyTradesTimeframes;
        this.printTasksHistory = printTasksHistory;
        this.reportLog = "";

        dummyTradesSummaryTable = new DummyTradesSummaryTable(DUMMY_TRADES_SUMMARY_FILE_NAME);

        dtm = new DummyTradesManager(initialBalance,
                minimumBalance,
                expectedBalanceX,
                maxTrades,
                tradeGenerator,
                tradeSimulator,
                coreData,
                writeDummyTradesReports,
                dummyTradesSummaryTable,
                DUMMY_TRADES_FOLDER_PATH,
                dataProcessor);
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
        dataProcessor.printCoreData(coreData);
    }

    private void printPricePredictionSummary() {

        long positivePricePredictions;
        long negativePricePredictions;
        long zeroPricePredictions;
        double minPricePrediction;
        double maxPricePrediction;
        double totalPriceVariation;

        if (coreData == null || coreData.hasInvalidStructure()) {
            log.error("Cannot print price prediction summary: core data is broken.");
            return;
        }

        List<Pattern> tradingPatterns = coreData.getTradingPatterns();

        positivePricePredictions = tradingPatterns.stream()
                .filter(pattern -> ((PredictivePattern) pattern).getPriceVariationPrediction() > 0)
                .count();

        negativePricePredictions = tradingPatterns.stream()
                .filter(pattern -> ((PredictivePattern) pattern).getPriceVariationPrediction() < 0)
                .count();

        zeroPricePredictions = tradingPatterns.stream()
                .filter(pattern -> ((PredictivePattern) pattern).getPriceVariationPrediction() == 0)
                .count();

        totalPriceVariation = tradingPatterns.stream()
                .mapToDouble(pattern -> ((PredictivePattern) pattern).getPriceVariationPrediction())
                .sum();

        minPricePrediction = tradingPatterns.stream()
                .mapToDouble(pattern -> ((PredictivePattern) pattern).getPriceVariationPrediction())
                .min().orElse(0);

        maxPricePrediction = tradingPatterns.stream()
                .mapToDouble(pattern -> ((PredictivePattern) pattern).getPriceVariationPrediction())
                .max().orElse(0);

        log.info(NEW_LINE
                        + "Positive price predictions: {}" + NEW_LINE
                        + "Negative price predictions: {}" + NEW_LINE
                        + "Zero-valued price predictions: {}" + NEW_LINE
                        + "Minimum price predictions: {}" + NEW_LINE
                        + "Maximum price predictions: {}" + NEW_LINE
                        + "AVERAGE PRICE PREDICTION: {}" + NEW_LINE,
                positivePricePredictions,
                negativePricePredictions,
                zeroPricePredictions,
                minPricePrediction,
                maxPricePrediction,
                totalPriceVariation / tradingPatterns.size());
    }


    private void runBasicDummyTrades() {
        if (coreData.hasInvalidStructure()) {
            log.error(DATA_MISSING_ERROR);
        } else {

            Optional<Graph> graph = coreData.getReadOnlyGraphs().stream().findAny();

            graph.ifPresent(value -> reportLog = dtm.launchDummyTrades(value, coreData, true, reportLog));
        }
    }

    private void runRandomizedDummyTrades() {
        runRandomizedDummyTrades(coreData);
    }

    private void runRandomizedDummyTrades(CoreData coreData) {
        if (coreData.hasInvalidStructure()) {
            log.error(DATA_MISSING_ERROR);
        } else {

            ProgressBar pb = new ProgressBar("Processing randomized trades...", dummyTradesTimeframes.size());

            pb.start();

            for (Graph graph : coreData.getReadOnlyGraphs()) {

                if (dummyTradesTimeframes.contains(graph.getTimeframe())) {
                    reportLog = dtm.launchDummyTrades(graph, coreData, true, reportLog);
                    pb.step();
                }

            }
            pb.stop();
        }
    }

    private void runRandomizedDummyTradesOnDummyGraphs() {
        runRandomizedDummyTrades(cloneCoreDataWithDummyGraphs());
    }

    private void runDeterministicDummyTradesOnDummyGraphs() {
        runDeterministicDummyTrades(cloneCoreDataWithDummyGraphs());
    }

    private void runDeterministicDummyTrades(CoreData coreData) {
        if (coreData.hasInvalidStructure()) {
            log.error(DATA_MISSING_ERROR);
        } else {

            ProgressBar pb = new ProgressBar("Processing deterministic trades...", dummyTradesTimeframes.size());

            pb.start();

            for (Graph graph : coreData.getReadOnlyGraphs()) {

                if (dummyTradesTimeframes.contains(graph.getTimeframe())) {
                    reportLog = dtm.launchDummyTrades(graph, coreData, false, reportLog);
                    pb.step();
                }

            }
            pb.stop();
        }
    }

    private @NonNull CoreData cloneCoreDataWithDummyGraphs() {
        CoreData dummyGraphsData = DefaultCoreData.newInstance();

        dummyGraphsData.copy(coreData);
        dummyGraphsData.purgeUselessData(PurgeOption.GRAPHS_AND_PATTERNS);
        dataProcessor.loadGraphs(dummyGraphsData, dummyGraphsDataFolderName, dummyGraphsDataFilesNames);
        dataProcessor.createGraphsForMissingTimeframes(dummyGraphsData);

        return dummyGraphsData;
    }

    //TODO Vérifier pourquoi la mise à jour du fichier dummy_trades_summary.csv ne fonctionne pas
    private synchronized void writeDummyTradesReports() {
        log.info(reportLog);
        CSVWriter.writeCSVDataToFile(DUMMY_TRADES_FOLDER_PATH + DUMMY_TRADES_SUMMARY_FILE_NAME, dummyTradesSummaryTable);
        reportLog = "";
    }

}
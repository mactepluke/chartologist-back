package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.*;
import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.pattern_recognition.PatternComputer;
import co.syngleton.chartomancer.trading.*;
import co.syngleton.chartomancer.util.Format;
import co.syngleton.chartomancer.util.csvwritertool.CSVWriter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.jetbrains.annotations.Contract;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Log4j2
final class Automation {
    private static final int MAX_BLANK_TRADE_MULTIPLIER = 10;
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
    private final AutomationTradingService automationTradingService;
    private final PatternComputer patternComputer;
    private final DummyTradesSummaryTable dummyTradesSummaryTable;
    private final double initialBalance;
    private final double minimumBalance;
    private final int maxTrades;
    private final double expectedBalanceX;
    private final boolean writeReports;
    private String reportLog;

    public Automation(CoreData coreData,
                      DataProcessor dataProcessor,
                      PatternComputer patternComputer,
                      AutomationTradingService automationTradingService,
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
        this.automationTradingService = automationTradingService;
        this.printCoreData = printCoreData;
        this.printPricePredictionSummary = printPricePredictionSummary;
        this.runBasicDummyTrades = runBasicDummyTrades;
        this.runRandomizedDummyTrades = runRandomizedDummyTrades;
        this.runRandomizedDummyTradesOnDummyGraphs = runRandomizedDummyTradesOnDummyGraphs;
        this.runDeterministicDummyTradesOnDummyGraphs = runDeterministicDummyTradesOnDummyGraphs;
        this.writeDummyTradeReports = writeDummyTradesReports;
        this.dummyGraphsDataFolderName = dummyGraphsDataFolderName;
        this.dummyGraphsDataFilesNames = dummyGraphsDataFilesNames;
        this.initialBalance = initialBalance;
        this.minimumBalance = minimumBalance;
        this.maxTrades = maxTrades;
        this.expectedBalanceX = expectedBalanceX;
        this.writeReports = writeDummyTradesReports;

        if (dummyTradesTimeframes == null || dummyTradesTimeframes.isEmpty()) {
            dummyTradesTimeframes = EnumSet.of(
                    Timeframe.SECOND,
                    Timeframe.MINUTE,
                    Timeframe.HALF_HOUR,
                    Timeframe.HOUR,
                    Timeframe.FOUR_HOUR,
                    Timeframe.DAY,
                    Timeframe.WEEK
            );
        }

        this.dummyTradesTimeframes = dummyTradesTimeframes;
        this.printTasksHistory = printTasksHistory;
        this.reportLog = "";

        dummyTradesSummaryTable = new DummyTradesSummaryTable(DUMMY_TRADES_SUMMARY_FILE_NAME);
        tasksHistory = new ArrayList<>();
    }

    public void launch() {
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
            log.info(this.coreData);
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

            graph.ifPresent(value -> reportLog = launchDummyTrades(value, coreData, true, reportLog));
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
                    reportLog = launchDummyTrades(graph, coreData, true, reportLog);
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
                    reportLog = launchDummyTrades(graph, coreData, false, reportLog);
                    pb.step();
                }

            }
            pb.stop();
        }
    }

    private @NonNull CoreData cloneCoreDataWithDummyGraphs() {
        CoreData dummyGraphsData = DefaultCoreData.copyOf(coreData);

        dummyGraphsData.purgeUselessData(PurgeOption.GRAPHS_AND_PATTERNS);
        dataProcessor.loadGraphs(dummyGraphsData, dummyGraphsDataFolderName, dummyGraphsDataFilesNames);
        dataProcessor.createGraphsForMissingTimeframes(dummyGraphsData);

        return dummyGraphsData;
    }

    private synchronized void writeDummyTradesReports() {
        log.info(reportLog);
        CSVWriter.writeCSVDataToFile(DUMMY_TRADES_FOLDER_PATH + DUMMY_TRADES_SUMMARY_FILE_NAME, dummyTradesSummaryTable);
        reportLog = "";
    }

    private String launchDummyTrades(@NonNull Graph graph, @NonNull CoreData coreData, boolean randomized, String reportLog) {

        TradingAccount account = new TradingAccount();

        account.credit(initialBalance);
        account.setName("Dummy Trade Account_randomized=" + randomized + "_" + graph.getSymbol() + "_" + graph.getTimeframe());

        if (coreData.canProvideDataForTradingOn(graph.getSymbol(), graph.getTimeframe())) {

            reportLog = reportLog + "*** TRADING WITH ACCOUNT:  " + account.getName() + " ***" + NEW_LINE;

            int blankTradesCount;

            TradingConditionsChecker conditionsChecker = TradingConditionsChecker.builder()
                    .maxTrades(maxTrades)
                    .maxBlankTrades(maxTrades * MAX_BLANK_TRADE_MULTIPLIER)
                    .minimumAccountBalance(minimumBalance)
                    .maximumAccountBalance(initialBalance * expectedBalanceX)
                    .build();

            TradingSimulationResult result = randomized ? automationTradingService.simulateTrades(TradeSimulationStrategy.randomize(graph, coreData, account), conditionsChecker)
                    : automationTradingService.simulateTrades(TradeSimulationStrategy.iterate(graph, coreData, account), conditionsChecker);

            blankTradesCount = result.blankTradeCount();

            String textResultSuffix = getAccountSolvabilityTag(result.account());

            reportLog = reportLog + generateTradesReport(account,
                    textResultSuffix,
                    result.account().getNumberOfLongs(),
                    account.getNumberOfShorts(),
                    result.blankTradeCount(),
                    result.usefulToUselessTradesRatio(),
                    result.totalDurationInDays(),
                    result.annualizedReturnPercentage());

            String fileName = Format.toFileNameCompatibleDateTime(LocalDateTime.now()) + "_" + account.getName() + "_" + graph.getName() + "_" + "_" + textResultSuffix;

            addDummyTradeEntry(getNewTradesSummaryEntry(
                    coreData,
                    account,
                    fileName,
                    graph.getSymbol(),
                    graph.getTimeframe(),
                    coreData.getMaxTradingScope(graph.getSymbol(), graph.getTimeframe()),
                    coreData.getTradingPatternLength(graph.getSymbol(), graph.getTimeframe()),
                    textResultSuffix,
                    result.account().getNumberOfLongs(),
                    result.account().getNumberOfShorts(),
                    blankTradesCount,
                    (float) result.usefulToUselessTradesRatio(),
                    result.totalDurationInDays(),
                    result.annualizedReturnPercentage()
            ));

            if (writeReports && account.getNumberOfTrades() > 0) {
                CSVWriter.writeCSVDataToFile(DUMMY_TRADES_FOLDER_PATH + fileName, account);
            }
        }
        return reportLog;
    }

    @Contract("_, _, _, _, _, _, _, _, _, _, _, _, _, _ -> new")
    private @NonNull DummyTradesSummaryEntry getNewTradesSummaryEntry(@NonNull CoreData coreData,
                                                                      @NonNull TradingAccount account,
                                                                      String fileName,
                                                                      Symbol symbol,
                                                                      Timeframe timeframe,
                                                                      int maxScope,
                                                                      int patternLength,
                                                                      String result,
                                                                      long longCount,
                                                                      long shortCount,
                                                                      long blankTradesCount,
                                                                      float usefulToUselessTradesRatio,
                                                                      double totalDuration,
                                                                      double annualizedReturnPercentage) {

        Map<CoreDataSettingNames, String> settings = automationTradingService.getAnalyzerSettingsSnapshot();

        Objects.requireNonNull(settings);


        return new DummyTradesSummaryEntry(
                Format.toFrenchDateTime(LocalDateTime.now()),
                coreData.getTradingPatternSetting(CoreDataSettingNames.COMPUTATION_DATE),
                fileName,
                dummyTradesSummaryTable.getFileName(),
                symbol.toString(),
                timeframe.toString(),
                coreData.getTradingPatternSetting(CoreDataSettingNames.MATCH_SCORE_SMOOTHING),
                coreData.getTradingPatternSetting(CoreDataSettingNames.MATCH_SCORE_THRESHOLD),
                coreData.getTradingPatternSetting(CoreDataSettingNames.PRICE_VARIATION_THRESHOLD),
                coreData.getTradingPatternSetting(CoreDataSettingNames.EXTRAPOLATE_PRICE_VARIATION),
                coreData.getTradingPatternSetting(CoreDataSettingNames.EXTRAPOLATE_MATCH_SCORE),
                coreData.getTradingPatternSetting(CoreDataSettingNames.PATTERN_AUTOCONFIG),
                coreData.getTradingPatternSetting(CoreDataSettingNames.COMPUTATION_AUTOCONFIG),
                coreData.getTradingPatternSetting(CoreDataSettingNames.COMPUTATION_TYPE),
                coreData.getTradingPatternSetting(CoreDataSettingNames.COMPUTATION_PATTERN_TYPE),
                coreData.getTradingPatternSetting(CoreDataSettingNames.ATOMIC_PARTITION),
                Integer.toString(maxScope),
                coreData.getTradingPatternSetting(CoreDataSettingNames.FULL_SCOPE),
                Integer.toString(patternLength),
                coreData.getTradingPatternSetting(CoreDataSettingNames.PATTERN_GRANULARITY),
                settings.get(CoreDataSettingNames.MATCH_SCORE_SMOOTHING),
                settings.get(CoreDataSettingNames.MATCH_SCORE_THRESHOLD),
                settings.get(CoreDataSettingNames.PRICE_VARIATION_THRESHOLD),
                settings.get(CoreDataSettingNames.EXTRAPOLATE_PRICE_VARIATION),
                settings.get(CoreDataSettingNames.EXTRAPOLATE_MATCH_SCORE),
                automationTradingService.getTradingProperties().rewardToRiskRatio(),
                automationTradingService.getTradingProperties().riskPercentage(),
                automationTradingService.getTradingProperties().priceVariationMultiplier(),
                automationTradingService.getTradingProperties().slTpStrategy(),
                maxTrades,
                result,
                initialBalance,
                initialBalance * expectedBalanceX,
                account.getBalance(),
                minimumBalance,
                account.getNumberOfTrades(),
                longCount,
                shortCount,
                blankTradesCount,
                usefulToUselessTradesRatio,
                account.getTotalPnl(),
                account.getLongPnl(),
                account.getShortPnl(),
                account.getTotalWinToLossRatio(),
                account.getLongWinToLossRatio(),
                account.getShortWinToLossRatio(),
                account.getTotalAveragePnL(),
                account.getLongAveragePnL(),
                account.getShortAveragePnL(),
                account.getTotalReturnPercentage(),
                account.getLongReturnPercentage(),
                account.getShortReturnPercentage(),
                account.getProfitFactor(),
                account.getProfitFactorQualification(),
                totalDuration,
                annualizedReturnPercentage

        );
    }

    private @NonNull String generateTradesReport(@NonNull TradingAccount account,
                                                 String result,
                                                 long longCount,
                                                 long shortCount,
                                                 long blankTradesCount,
                                                 double usefulToUselessTradesRatio,
                                                 double totalDuration,
                                                 double annualizedReturnPercentage) {

        String cur = account.getCurrency();

        return
                "TRADING SETTINGS: " +
                        automationTradingService.getTradingProperties().toString() + NEW_LINE + NEW_LINE +
                        "*** ADVANCED DUMMY TRADE RESULTS ***" + NEW_LINE +
                        "Result: " + result + NEW_LINE +
                        "Number of dummy trades performed: " + account.getNumberOfTrades() + NEW_LINE +
                        "Number of longs: " + longCount + NEW_LINE +
                        "Number of shorts: " + shortCount + NEW_LINE +
                        "Number of useless trades: " + blankTradesCount + NEW_LINE +
                        "Used to Useless trade ratio: " + usefulToUselessTradesRatio + NEW_LINE +
                        "Initial balance: " + cur + " " + initialBalance + NEW_LINE +
                        "Target balance amount: " + cur + " " + (initialBalance * expectedBalanceX) + NEW_LINE +
                        "Final Account Balance: " + cur + " " + account.getBalance() + NEW_LINE +
                        "Total duration (in days): " + totalDuration + NEW_LINE +
                        "Annualized return %: " + annualizedReturnPercentage
                        + NEW_LINE +
                        account.generatePrintableTradesStats() + NEW_LINE;
    }

    private String getAccountSolvabilityTag(@NonNull TradingAccount account) {
        String result = "NEUTRAL";

        if (account.getBalance() > initialBalance * expectedBalanceX) {
            result = "RICH";
        } else if (account.getBalance() < minimumBalance || account.isLiquidated()) {
            result = "REKT";
        }
        return result;
    }

    private void addDummyTradeEntry(DummyTradesSummaryEntry dummyTradesSummaryEntry) {
        this.dummyTradesSummaryTable.getCSVData().add(dummyTradesSummaryEntry);
    }

}
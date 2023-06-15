package com.syngleton.chartomancy.automation;

import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.data.DataSettings;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.PatternBox;
import com.syngleton.chartomancy.model.charting.patterns.interfaces.ScopedPattern;
import com.syngleton.chartomancy.model.trading.Trade;
import com.syngleton.chartomancy.model.trading.TradeStatus;
import com.syngleton.chartomancy.model.trading.TradingAccount;
import com.syngleton.chartomancy.service.domain.DataService;
import com.syngleton.chartomancy.service.domain.PatternService;
import com.syngleton.chartomancy.service.domain.TradingService;
import com.syngleton.chartomancy.util.Check;
import com.syngleton.chartomancy.util.Format;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Log4j2
public class Automation implements Runnable {

    private static final String DUMMY_TRADES_FOLDER_PATH = "./trades_history/";
    private static final String DUMMY_TRADES_SUMMARY_FILE_NAME = "dummy_trades_summary";
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final boolean printCoreData;
    private final boolean printPricePredictionSummary;
    private final boolean runBasicDummyTrades;
    private final boolean runAdvancedDummyTrades;
    private final double initialBalance;
    private final double minimumBalance;
    private final int expectedBalanceX;
    private final int maxTrades;
    private final boolean writeDummyTradeReports;
    private final boolean printTasksHistory;
    private final DummyTradesSummaryTable dummyTradesSummaryTable;
    private final List<String> tasksHistory;
    CoreData coreData;
    DataService dataService;
    PatternService patternService;
    TradingService tradingService;
    private String reportLog = "";

    public Automation(CoreData coreData,
                      DataService dataService,
                      PatternService patternService,
                      TradingService tradingService,
                      boolean printCoreData,
                      boolean printPricePredictionSummary,
                      boolean runBasicDummyTrades,
                      boolean runAdvancedDummyTrades,
                      double initialBalance,
                      double minimumBalance,
                      int expectedBalanceX,
                      int maxTrades,
                      boolean writeDummyTradeReports,
                      boolean printTasksHistory) {
        this.coreData = coreData;
        this.dataService = dataService;
        this.patternService = patternService;
        this.tradingService = tradingService;
        this.printCoreData = printCoreData;
        this.printPricePredictionSummary = printPricePredictionSummary;
        this.runBasicDummyTrades = runBasicDummyTrades;
        this.runAdvancedDummyTrades = runAdvancedDummyTrades;
        this.initialBalance = initialBalance;
        this.minimumBalance = minimumBalance;
        this.expectedBalanceX = expectedBalanceX;
        this.maxTrades = maxTrades;
        this.writeDummyTradeReports = writeDummyTradeReports;
        this.printTasksHistory = printTasksHistory;

        dummyTradesSummaryTable = new DummyTradesSummaryTable();
        tasksHistory = new ArrayList<>();
    }

    @Override
    public void run() {
        log.info("*** AUTOMATION LAUNCHED ***");

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        log.debug("printCoreData {}," + NEW_LINE
                        + "printPricePredictionSummary {}," + NEW_LINE
                        + "runBasicDummyTrades {}," + NEW_LINE
                        + "runAdvancedDummyTrades {}," + NEW_LINE
                        + "writeDummyTradesReports {}," + NEW_LINE
                        + "printTasksHistory {}," + NEW_LINE,
                printCoreData,
                printPricePredictionSummary,
                runBasicDummyTrades,
                runAdvancedDummyTrades,
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
        if (runAdvancedDummyTrades) {
            runAdvancedDummyTrades();
            tasksHistory.add("RAN ADVANCED DUMMY TRADES");
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

    private void runBasicDummyTrades() {
        if (coreData == null
                || !Check.notNullNotEmpty(coreData.getGraphs())
                || !Check.notNullNotEmpty(coreData.getTradingPatternBoxes())) {
            log.error("Could not run dummy trades: core data are missing.");
        } else {
            clearLog();

            Optional<Graph> graph = coreData.getGraphs().stream().findAny();
            graph.ifPresent(this::runDummyTradesOnGraph);
        }

    }

    private void runAdvancedDummyTrades() {

        if (coreData == null
                || !Check.notNullNotEmpty(coreData.getGraphs())
                || !Check.notNullNotEmpty(coreData.getTradingPatternBoxes())) {
            log.error("Could not run dummy trades: core data are missing.");
        } else {
            clearLog();

            ProgressBar pb = new ProgressBar("Processing trades...", coreData.getGraphs().size());

            pb.start();

            for (Graph graph : coreData.getGraphs()) {
                runDummyTradesOnGraph(graph);

                pb.step();
            }
            pb.stop();
        }

    }

    private void runDummyTradesOnGraph(@NonNull Graph graph) {

        TradingAccount account = new TradingAccount();
        DummyTradesSummaryEntry dummyTradesSummaryEntry;

        account.credit(initialBalance);
        account.setName("Dummy Trade Account_" + graph.getSymbol() + "_" + graph.getTimeframe());

        Optional<PatternBox> optionalPatternBox = coreData.getTradingPatternBox(graph.getSymbol(), graph.getTimeframe());

        if (optionalPatternBox.isPresent()) {

            int maxScope = optionalPatternBox.get().getMaxScope();
            int patternLength = optionalPatternBox.get().getPatternLength();

            Trade trade;

            int blankTradesCount = 0;

            reportLog = reportLog + "*** TRADING WITH ACCOUNT: {} ***" + account.getName() + NEW_LINE;

            do {

                trade = generateAndProcessAdvancedRandomTrades(graph, account, maxScope, patternLength);

                if (trade != null && trade.getStatus() == TradeStatus.BLANK) {
                    blankTradesCount++;
                }

            } while (
                    trade == null
                            || trade.getStatus() == TradeStatus.BLANK
                            || (
                            !account.isLiquidated()
                                    && account.getBalance() > minimumBalance
                                    && account.getBalance() < initialBalance * expectedBalanceX
                                    && trade.getStatus() != TradeStatus.UNFUNDED
                                    && account.getNumberOfTrades() < maxTrades
                    )
            );

            String result = "NEUTRAL";
            if (account.getBalance() > initialBalance * expectedBalanceX) {
                result = "RICH";
            } else if (account.getBalance() < minimumBalance || account.isLiquidated()) {
                result = "REKT";
            }

            long longCount = account.getNumberOfLongs();
            long shortCount = account.getNumberOfShorts();
            float usefulToUselessTradesRatio = blankTradesCount == 0 ? -1 : Format.roundTwoDigits((longCount + shortCount) / (float) blankTradesCount);

            var cur = account.getCurrency();

            reportLog = reportLog +
                    "TRADING SETTINGS: {}" +
                    tradingService.printTradingSettings() + NEW_LINE + NEW_LINE +
                    "*** ADVANCED DUMMY TRADE RESULTS ***" + NEW_LINE +
                    "Result: " + result + NEW_LINE +
                    "Number of dummy trades performed: " + account.getNumberOfTrades() + NEW_LINE +
                    "Number of longs: " + longCount + NEW_LINE +
                    "Number of shorts: " + shortCount + NEW_LINE +
                    "Number of useless trades: " + blankTradesCount + NEW_LINE +
                    "Used / Useless trade ratio: " + usefulToUselessTradesRatio + NEW_LINE +
                    "Initial balance: " + cur + " " + initialBalance + NEW_LINE +
                    "Target balance amount: " + cur + " " + (initialBalance * expectedBalanceX) + NEW_LINE +
                    "Final Account Balance: " + cur + " " + account.getBalance() + NEW_LINE +
                    account.generatePrintableTradesStats() + NEW_LINE;

            DataSettings settings = coreData.getTradingPatternSettings();

            String fileName = account.getName() + "_" + Format.toFileNameCompatibleDateTime(LocalDateTime.now()) + "_" + result;

            dummyTradesSummaryEntry = new DummyTradesSummaryEntry(
                    Format.toFrenchDateTime(LocalDateTime.now()),
                    Format.toFrenchDateTime(settings.getComputationDate()),
                    fileName,
                    DUMMY_TRADES_SUMMARY_FILE_NAME,
                    optionalPatternBox.get().getSymbol(),
                    optionalPatternBox.get().getTimeframe(),
                    settings.getMatchScoreSmoothing(),
                    settings.getMatchScoreThreshold(),
                    settings.getPriceVariationThreshold(),
                    settings.isExtrapolatePriceVariation(),
                    settings.isExtrapolateMatchScore(),
                    settings.getPatternAutoconfig(),
                    settings.getComputationAutoconfig(),
                    settings.getComputationType(),
                    settings.getComputationPatternType(),
                    settings.isAtomicPartition(),
                    maxScope,
                    settings.isFullScope(),
                    patternLength,
                    settings.getPatternGranularity(),
                    tradingService.getTradingSettings().getRewardToRiskRatio(),
                    tradingService.getTradingSettings().getRiskPercentage(),
                    tradingService.getTradingSettings().getPriceVariationThreshold(),
                    tradingService.getTradingSettings().getPriceVariationMultiplier(),
                    tradingService.getTradingSettings().getSlTpStrategy(),
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
                    account.getLongWinToLossRatio(),
                    account.getTotalAveragePnL(),
                    account.getLongAveragePnL(),
                    account.getShortAveragePnL(),
                    account.getTotalReturnPercentage(),
                    account.getLongReturnPercentage(),
                    account.getShortReturnPercentage(),
                    account.getProfitFactor(),
                    account.getProfitFactorQualification()

            );

            addDummyTradeEntry(dummyTradesSummaryEntry);

            if (writeDummyTradeReports) {
                dataService.writeCsvFile(DUMMY_TRADES_FOLDER_PATH + fileName, account);
            }
        }
    }

    private synchronized void writeDummyTradesReports() {
        log.info(reportLog);
        dataService.writeCsvFile(DUMMY_TRADES_FOLDER_PATH + DUMMY_TRADES_SUMMARY_FILE_NAME, dummyTradesSummaryTable);
    }

    private void addDummyTradeEntry(DummyTradesSummaryEntry dummyTradesSummaryEntry) {
        this.dummyTradesSummaryTable.getPrintableData().add(dummyTradesSummaryEntry);
    }

    private Trade generateAndProcessAdvancedRandomTrades(Graph graph, TradingAccount account, int maxScope, int patternLength) {
        int tradeOpenCandle = ThreadLocalRandom.current().nextInt(graph.getFloatCandles().size() - maxScope - patternLength - 1) + patternLength;

        Trade trade = tradingService.generateParameterizedTrade(
                account,
                graph,
                coreData,
                tradeOpenCandle
        );

        if (trade != null && trade.getStatus() == TradeStatus.OPENED) {

            tradingService.processTradeOnCompletedCandles(
                    trade,
                    account,
                    graph.getFloatCandles().subList(tradeOpenCandle + 1, tradeOpenCandle + maxScope)
            );

        }

        return trade;
    }

    private void clearLog() {
        reportLog = "";
    }
}
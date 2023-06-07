package com.syngleton.chartomancy.analytics;

import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.model.charting.patterns.ComputablePattern;
import com.syngleton.chartomancy.model.charting.patterns.PatternBox;
import com.syngleton.chartomancy.model.trading.Trade;
import com.syngleton.chartomancy.model.trading.TradingAccount;
import com.syngleton.chartomancy.service.DataService;
import com.syngleton.chartomancy.service.PatternService;
import com.syngleton.chartomancy.model.trading.TradeStatus;
import com.syngleton.chartomancy.service.TradingService;
import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Log4j2
public class Automation implements Runnable {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final boolean printCoreData;
    private final boolean printPricePredictionSummary;
    private final boolean runBasicDummyTrades;
    private final boolean runAdvancedDummyTrades;
    private final boolean printTasksHistory;

    private final List<String> tasksHistory;

    CoreData coreData;
    DataService dataService;
    PatternService patternService;
    TradingService tradingService;

    public Automation(CoreData coreData,
                      DataService dataService,
                      PatternService patternService,
                      TradingService tradingService,
                      boolean printCoreData,
                      boolean printPricePredictionSummary,
                      boolean runBasicDummyTrades,
                      boolean runAdvancedDummyTrades,
                      boolean printTasksHistory) {
        this.coreData = coreData;
        this.dataService = dataService;
        this.patternService = patternService;
        this.tradingService = tradingService;
        this.printCoreData = printCoreData;
        this.printPricePredictionSummary = printPricePredictionSummary;
        this.runBasicDummyTrades = runBasicDummyTrades;
        this.runAdvancedDummyTrades = runAdvancedDummyTrades;
        this.printTasksHistory = printTasksHistory;

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
                        + "printTasksHistory {}," + NEW_LINE,
                printCoreData,
                printPricePredictionSummary,
                runBasicDummyTrades,
                runAdvancedDummyTrades,
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

        log.info(NEW_LINE +
                "ANALYSER CONFIG: {}" + NEW_LINE
                + "Using {} pattern box(es)", coreData.getAnalyzerConfigSettings(), coreData.getPatternBoxes().size());

        for (PatternBox patternBox : coreData.getPatternBoxes()) {

            positivePricePredictions = patternBox.getListOfAllPatterns().stream()
                    .filter(pattern -> ((ComputablePattern) pattern).getPriceVariationPrediction() > 0)
                    .count();

            negativePricePredictions = patternBox.getListOfAllPatterns().stream()
                    .filter(pattern -> ((ComputablePattern) pattern).getPriceVariationPrediction() < 0)
                    .count();

            zeroPricePredictions = patternBox.getListOfAllPatterns().stream()
                    .filter(pattern -> ((ComputablePattern) pattern).getPriceVariationPrediction() == 0)
                    .count();

            totalPriceVariation = patternBox.getListOfAllPatterns().stream()
                    .mapToDouble(pattern -> ((ComputablePattern) pattern).getPriceVariationPrediction())
                    .sum();

            minPricePrediction = patternBox.getListOfAllPatterns().stream()
                    .mapToDouble(pattern -> ((ComputablePattern) pattern).getPriceVariationPrediction())
                    .min().orElse(0);

            maxPricePrediction = patternBox.getListOfAllPatterns().stream()
                    .mapToDouble(pattern -> ((ComputablePattern) pattern).getPriceVariationPrediction())
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
    }

    private void runBasicDummyTrades() {

    }

    private void runAdvancedDummyTrades() {

        float initialBalance = 100000;
        float minimumBalance = initialBalance / 2;
        int expectedXToBeRich = 2;
        int maxTrades = 1500;

        TradingAccount account = new TradingAccount();

        account.credit(initialBalance);
        account.setName("Advanced Test Account");

        Symbol symbol = Symbol.BTC_USD;
        Timeframe timeframe = Timeframe.HOUR;

        Graph graph = coreData.getGraph(symbol, timeframe);

        if (graph != null) {
            Optional<PatternBox> optionalPatternBox = coreData.getTradingPatternBox(symbol, timeframe);

            if (optionalPatternBox.isPresent()) {

                int maxScope = optionalPatternBox.get().getMaxScope();
                int patternLength = optionalPatternBox.get().getPatternLength();

                Trade trade;

                int blankTradesCount = 0;

                ProgressBar pb = new ProgressBar("Processing trades...", maxTrades);

                pb.start();

                do {

                    trade = generateAndProcessAdvancedRandomTrades(graph, account, maxScope, patternLength);

                    if (trade != null && trade.getStatus() == TradeStatus.BLANK) {
                        blankTradesCount++;
                    }

                    pb.step();

                } while (
                        trade == null
                                || trade.getStatus() == TradeStatus.BLANK
                                || (
                                !account.isLiquidated()
                                        && account.getBalance() > minimumBalance
                                        && account.getBalance() < initialBalance * expectedXToBeRich
                                        && trade.getStatus() != TradeStatus.UNFUNDED
                                        && account.getNumberOfTrades() < maxTrades
                        )
                );

                pb.stop();

                String result = "NEUTRAL";
                if (account.getBalance() > initialBalance * expectedXToBeRich) {
                    result = "RICH";
                } else if (account.getBalance() < minimumBalance || account.isLiquidated()) {
                    result = "REKT";
                }

                long longCount = account.getNumberOfLongs();
                long shortCount = account.getNumberOfShorts();

                log.info("TRADING SETTINGS: {}", tradingService.printTradingSettings());

                log.info(
                        NEW_LINE + "ADVANCED DUMMY TRADE RESULTS:" + NEW_LINE +
                                "Result: {}" + NEW_LINE +
                                "Number of dummy trades performed: {}" + NEW_LINE +
                                "Number of longs: {}" + NEW_LINE +
                                "Number of shorts: {}" + NEW_LINE +
                                "Number of useless trades: {}" + NEW_LINE +
                                "Used / Useless trade ratio= {}" + NEW_LINE +
                                "Initial balance: {} {}" + NEW_LINE +
                                "Target balance amount: {} {}" + NEW_LINE +
                                "Final Account Balance: {} {}" + NEW_LINE +
                                "{}",
                        result,
                        account.getNumberOfTrades(),
                        longCount,
                        shortCount,
                        blankTradesCount,
                        blankTradesCount == 0 ? "Infinity" : Format.roundTwoDigits((longCount + shortCount) / (float) blankTradesCount),
                        account.getCurrency(), initialBalance,
                        account.getCurrency(), initialBalance * expectedXToBeRich,
                        account.getCurrency(), account.getBalance(),
                        account.generatePrintableTradesStats()
                );

                dataService.writeCsvFile("./trades_history/" + account.getName() + "_" + result + "_" + LocalDateTime.now(), account);
            }
        } else {
            log.error("Could not process advanced dummy trades = core data is missing.");
        }
    }

    private Trade generateAndProcessAdvancedRandomTrades(Graph graph, TradingAccount account, int maxScope, int patternLength)    {
        int tradeOpenCandle = ThreadLocalRandom.current().nextInt(graph.getFloatCandles().size() - maxScope - patternLength - 1) + patternLength;

        Trade trade = tradingService.generateOptimalLeveragedTakeProfitBasedTrade(
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

}
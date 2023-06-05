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
import com.syngleton.chartomancy.service.TradeStatus;
import com.syngleton.chartomancy.service.TradingService;
import com.syngleton.chartomancy.util.pdt.PDT;
import lombok.extern.log4j.Log4j2;
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
                + "Analysing {} pattern box(es)...", patternService.printAnalyzerConfig(), coreData.getPatternBoxes().size());

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

/*        TradingAccount account = new TradingAccount();
        account.credit(100000);
        account.setName("Basic Test Account");

        Trade trade1 = new Trade("Binance",
                Timeframe.DAY,
                Symbol.BTC_USD,
                account,
                LocalDateTime.now(),
                LocalDateTime.now(),
                true,
                1000,
                1);

        Trade trade2 = new Trade("Coinbase",
                Timeframe.HOUR,
                Symbol.BTC_USD,
                account,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                500,
                2);

        Trade trade3 = new Trade("Bitfinex",
                Timeframe.FOUR_HOUR,
                Symbol.BTC_USD,
                account,
                LocalDateTime.now(),
                LocalDateTime.now(),
                true,
                300,
                1);

        trade3.closeTrade(LocalDateTime.now(), 30000, TradeStatus.CLOSED_MANUALLY);

        log.debug(trade1);
        log.debug(trade1.toRow());
        log.debug(trade2);
        log.debug(trade2.toRow());
        log.debug(trade3);
        log.debug(trade3.toRow());


        account.getTrades().addAll(List.of(trade1, trade2, trade3));


        log.debug("TRADES TABLE SIZE={}", account.getTrades().size());
        log.debug("TRADES TABLE SIZE (ROWS)={}", account.getPrintableData().size());
        log.debug("TRADES TABLE HEADER SIZE={}", account.getHeader().size());
        log.debug("TRADES TABLE HEADER SIZE={}", account.getPrintableData().get(0).toRow().size());
        log.debug("TRADES TABLE VALUE SEPARATOR={}", account.getRowValuesSeparator());

        PDT.writeDataTableToFile("./trades_history/" + account.getName() + "_" + LocalDateTime.now() +
                "", account);*/


    }

    private void runAdvancedDummyTrades() {

        float initialBalance = 100000;
        int expectedXToBeRich = 2;

        TradingAccount account = new TradingAccount();

        account.credit(initialBalance);
        account.setName("Advanced Test Account");

        Symbol symbol = Symbol.BTC_USD;
        Timeframe timeframe = Timeframe.HOUR;

        boolean isRich;

        Graph graph = coreData.getGraph(symbol, timeframe);

        if (graph != null) {
            int graphSize = graph.getFloatCandles().size();
            Optional<PatternBox> optionalPatternBox = coreData.getTradingPatternBox(symbol, timeframe);

            if (optionalPatternBox.isPresent()) {

                int maxScope = optionalPatternBox.get().getMaxScope();
                int patternLength = optionalPatternBox.get().getPatternLength();

                Trade trade;

                do {
                    int tradeOpenCandle = ThreadLocalRandom.current().nextInt(graphSize - maxScope - patternLength) + patternLength;

                    trade = tradingService.generateOptimalLeveragedTakeProfitBasedTrade(
                            account,
                            graph,
                            coreData,
                            tradeOpenCandle
                    );

                    tradingService.processTradeOnCompletedCandles(trade, account, graph.getFloatCandles().subList(tradeOpenCandle + 1, maxScope));

                    isRich = account.getBalance() > initialBalance * expectedXToBeRich;

                } while (!account.isLiquidated() && !isRich && trade.getStatus() != TradeStatus.UNFUNDED);

                if (isRich) {
                    log.info("Test user is rich!");
                } else {
                    log.info("Test user is liquidated!");
                }

                PDT.writeDataTableToFile("./trades_history/" + account.getName() + "_" + LocalDateTime.now() +
                        "", account);
            }
        } else {
            log.error("Could not process advanced dummy trades = core data is missing.");
        }
    }

}
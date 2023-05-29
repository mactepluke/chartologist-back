package com.syngleton.chartomancy.analytics;

import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.model.charting.patterns.ComputablePattern;
import com.syngleton.chartomancy.model.charting.patterns.PatternBox;
import com.syngleton.chartomancy.model.trading.TradingAccount;
import com.syngleton.chartomancy.service.DataService;
import com.syngleton.chartomancy.service.PatternService;
import com.syngleton.chartomancy.service.TradingService;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
public class Automation implements Runnable {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final boolean printCoreData;
    private final boolean printPricePredictionSummary;
    private final boolean runBasicDummyTrades;
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
                      boolean printTasksHistory) {
        this.coreData = coreData;
        this.dataService = dataService;
        this.patternService = patternService;
        this.tradingService = tradingService;
        this.printCoreData = printCoreData;
        this.printPricePredictionSummary = printPricePredictionSummary;
        this.runBasicDummyTrades = runBasicDummyTrades;
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
                + "printTasksHistory {}," + NEW_LINE,
                printCoreData,
                printPricePredictionSummary,
                runBasicDummyTrades,
                printTasksHistory
                );

        if (printCoreData)  {
            printCoreData();
            tasksHistory.add("PRINTED CORE DATA");
        }
        if (printPricePredictionSummary)  {
            printPricePredictionSummary();
            tasksHistory.add("PRINTED PRICE PREDICTION SUMMARY");
        }
        if (runBasicDummyTrades)  {
            runBasicDummyTrades();
            tasksHistory.add("RAN BASIC DUMMY TRADES");
        }
        if (printTasksHistory)  {
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

        TradingAccount account = new TradingAccount();

        //dataService.generateCsv(account.exportTradesToCsv());
/*        Graph graph = coreData.getGraph(Symbol.BTC_USD, Timeframe.HOUR);

        for (var i = 1; i < 3000; i++) {
            Trade trade = tradingService.generateOptimalBasicTrade(graph, coreData, 200 * i, -1, 0);
            if (trade == null) {
                break;
            }
            log.info("TRADE# {} -------> {}", i + 1, trade);
        }*/
    }

    private void runAdvancedDummyTrades()   {


    }

}
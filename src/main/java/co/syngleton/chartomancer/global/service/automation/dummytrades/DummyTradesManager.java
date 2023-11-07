package co.syngleton.chartomancer.global.service.automation.dummytrades;

import co.syngleton.chartomancer.analytics.data.CoreData;
import co.syngleton.chartomancer.analytics.data.DataSettings;
import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.PatternBox;
import co.syngleton.chartomancer.analytics.model.Symbol;
import co.syngleton.chartomancer.analytics.model.Timeframe;
import co.syngleton.chartomancer.trading.service.TradingService;
import co.syngleton.chartomancer.trading.model.Trade;
import co.syngleton.chartomancer.trading.model.TradeStatus;
import co.syngleton.chartomancer.trading.model.TradingAccount;
import co.syngleton.chartomancer.analytics.service.DataService;
import co.syngleton.chartomancer.global.tools.Calc;
import co.syngleton.chartomancer.global.tools.Format;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Contract;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.round;

@Log4j2
public class DummyTradesManager {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final int MAX_BLANK_TRADE_MULTIPLIER = 1;
    private final double initialBalance;
    private final double minimumBalance;
    private final int expectedBalanceX;
    private final int maxTrades;
    private final TradingService tradingService;
    private final CoreData coreData;
    private final DummyTradesSummaryTable dummyTradesSummaryTable;
    private final boolean writeReports;
    private final String dummyTradesFolderPath;
    private final DataService dataService;

    public DummyTradesManager(double initialBalance,
                              double minimumBalance,
                              int expectedBalanceX,
                              int maxTrades,
                              TradingService tradingService,
                              CoreData coreData,
                              boolean writeReports,
                              DummyTradesSummaryTable dummyTradesSummaryTable,
                              String dummyTradesFolderPath,
                              DataService dataService) {
        this.initialBalance = initialBalance;
        this.minimumBalance = minimumBalance;
        this.expectedBalanceX = expectedBalanceX;
        this.maxTrades = maxTrades;
        this.tradingService = tradingService;
        this.coreData = coreData;
        this.writeReports = writeReports;
        this.dummyTradesSummaryTable = dummyTradesSummaryTable;
        this.dummyTradesFolderPath = dummyTradesFolderPath;
        this.dataService = dataService;
    }

    public String launchDummyTrades(@NonNull Graph graph, @NonNull CoreData coreData, boolean randomized, String reportLog) {

        TradingAccount account = new TradingAccount();

        account.credit(initialBalance);
        account.setName("Dummy Trade Account_randomized=" + randomized + "_" + graph.getSymbol() + "_" + graph.getTimeframe());

        Optional<PatternBox> optionalPatternBox = coreData.getTradingPatternBox(graph.getSymbol(), graph.getTimeframe());

        if (optionalPatternBox.isPresent()) {

            int maxScope = optionalPatternBox.get().getMaxScope();
            int patternLength = optionalPatternBox.get().getPatternLength();

            reportLog = reportLog + "*** TRADING WITH ACCOUNT: {} ***" + account.getName() + NEW_LINE;

            int blankTradesCount;

            blankTradesCount = randomized ? generateAndProcessRandomTrades(graph, account, maxScope, patternLength)
                    : generateAndProcessDeterministicTrades(graph, account, maxScope, patternLength);


            String result = checkAccount(account);

            long longCount = account.getNumberOfLongs();
            long shortCount = account.getNumberOfShorts();
            float usefulToUselessTradesRatio = blankTradesCount == 0 ? -1 : Format.roundTwoDigits((longCount + shortCount) / (float) blankTradesCount);
            var totalDurationInSeconds = getDummyTradesDurationInSeconds(account, blankTradesCount, graph.getTimeframe());
            double totalDuration = Format.roundTwoDigits(totalDurationInSeconds / (double) Timeframe.DAY.durationInSeconds);
            double annualizedReturnPercentage = Format.roundTwoDigits(
                    (356 * Timeframe.DAY.durationInSeconds * Calc.relativePercentage(account.getTotalPnl(), initialBalance)) / (double) totalDurationInSeconds);

            reportLog = reportLog + generateTradesReport(account,
                    result,
                    longCount,
                    shortCount,
                    blankTradesCount,
                    usefulToUselessTradesRatio,
                    totalDuration,
                    annualizedReturnPercentage);

            DataSettings settings = coreData.getTradingPatternSettings();

            String fileName = Format.toFileNameCompatibleDateTime(LocalDateTime.now()) + "_" + account.getName() + "_" + graph.getName() + "_" + "_" + result;

            addDummyTradeEntry(getNewTradesSummaryEntry(
                    settings,
                    account,
                    fileName,
                    optionalPatternBox.get().getSymbol(),
                    optionalPatternBox.get().getTimeframe(),
                    maxScope,
                    patternLength,
                    result,
                    longCount,
                    shortCount,
                    blankTradesCount,
                    usefulToUselessTradesRatio,
                    totalDuration,
                    annualizedReturnPercentage
            ));

            if (writeReports) {
                dataService.writeCsvFile(dummyTradesFolderPath + fileName, account);
            }
        }
        return reportLog;
    }

    private int generateAndProcessRandomTrades(@NonNull Graph graph, TradingAccount account, int maxScope, int patternLength) {

        int blankTradesCount = 0;
        int tradeOpenCandle;
        int bound = graph.getFloatCandles().size() - maxScope - patternLength - 1;
        boolean liquidated = false;

        do {
            tradeOpenCandle = ThreadLocalRandom.current().nextInt(Math.max(bound, 1)) + patternLength;

            Trade trade = generateAndProcessTrade(graph, account, maxScope, tradeOpenCandle);

            if (trade != null && trade.getStatus() == TradeStatus.BLANK) {
                blankTradesCount++;
            }

            if (trade != null && trade.getStatus() == TradeStatus.UNFUNDED) {
                liquidated = true;
            }

        } while (blankTradesCount < MAX_BLANK_TRADE_MULTIPLIER * maxTrades
                && account.getNumberOfTrades() < maxTrades
                && !account.isLiquidated()
                && !liquidated
                && account.getBalance() > minimumBalance
                && account.getBalance() < initialBalance * expectedBalanceX);

        return blankTradesCount;
    }

    private int generateAndProcessDeterministicTrades(@NonNull Graph graph, @NonNull TradingAccount account, int maxScope, int patternLength) {

        int blankTradesCount = 0;
        int tradeOpenCandle = patternLength;
        int bound = graph.getFloatCandles().size() - maxScope;
        boolean liquidated = false;

        while (!account.isLiquidated()
                && account.getBalance() > minimumBalance
                && account.getBalance() < initialBalance * expectedBalanceX
                && tradeOpenCandle < bound
                && !liquidated) {

            Trade trade = generateAndProcessTrade(graph, account, maxScope, tradeOpenCandle);

            if (trade != null) {
                if (trade.getStatus() == TradeStatus.BLANK) {
                    blankTradesCount++;
                    tradeOpenCandle++;
                } else if (trade.getStatus() == TradeStatus.UNFUNDED) {
                    liquidated = true;
                } else {
                    tradeOpenCandle = tradeOpenCandle + (round((trade.getCloseDateTime().toEpochSecond(ZoneOffset.UTC) - trade.getOpenDateTime().toEpochSecond(ZoneOffset.UTC))
                            / (float) trade.getTimeframe().durationInSeconds) + 1);
                }
            }
        }
        return blankTradesCount;
    }

    private Trade generateAndProcessTrade(@NonNull Graph graph, TradingAccount account, int maxScope, int tradeOpenCandle) {

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
                    graph.getFloatCandles().subList(tradeOpenCandle, tradeOpenCandle + maxScope)
            );
        }
        return trade;
    }

    @Contract("_, _, _, _, _, _, _, _, _, _, _, _, _, _ -> new")
    private @NonNull DummyTradesSummaryEntry getNewTradesSummaryEntry(@NonNull DataSettings settings,
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
        return new DummyTradesSummaryEntry(
                Format.toFrenchDateTime(LocalDateTime.now()),
                Format.toFrenchDateTime(settings.getComputationDate()),
                fileName,
                dummyTradesSummaryTable.getFileName(),
                symbol,
                timeframe,
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
                tradingService.getAnalyzer().getMatchScoreSmoothing(),
                tradingService.getAnalyzer().getMatchScoreThreshold(),
                tradingService.getTradingSettings().getPriceVariationThreshold(),
                tradingService.getAnalyzer().isExtrapolatePriceVariation(),
                tradingService.getAnalyzer().isExtrapolateMatchScore(),
                tradingService.getTradingSettings().getRewardToRiskRatio(),
                tradingService.getTradingSettings().getRiskPercentage(),
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
                                                 float usefulToUselessTradesRatio,
                                                 double totalDuration,
                                                 double annualizedReturnPercentage) {

        String cur = account.getCurrency();

        return
                "TRADING SETTINGS: {}" +
                        tradingService.printTradingSettings() + NEW_LINE + NEW_LINE +
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

    private long getDummyTradesDurationInSeconds(@NonNull TradingAccount account, long blankTradeCount, @NonNull Timeframe timeframe) {
        return (account.getTotalTradeDurationsInSeconds() + blankTradeCount * timeframe.durationInSeconds);
    }

    private String checkAccount(@NonNull TradingAccount account) {
        String result = "NEUTRAL";

        if (account.getBalance() > initialBalance * expectedBalanceX) {
            result = "RICH";
        } else if (account.getBalance() < minimumBalance || account.isLiquidated()) {
            result = "REKT";
        }
        return result;
    }


    private void addDummyTradeEntry(DummyTradesSummaryEntry dummyTradesSummaryEntry) {
        this.dummyTradesSummaryTable.getPrintableData().add(dummyTradesSummaryEntry);
    }

}

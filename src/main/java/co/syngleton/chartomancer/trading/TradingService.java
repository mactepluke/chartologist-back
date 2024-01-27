package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.shared_domain.*;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import co.syngleton.chartomancer.util.Triad;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Math.abs;

@Log4j2
@Service
@AllArgsConstructor
class TradingService implements TradeGenerator, TradeSimulator {

    @Getter
    private final Analyzer tradingAnalyzer;
    private final CandleRescaler candleRescaler;
    @Getter
    private final TradingProperties tradingProperties;

    /**
     * This method finds the best price variation percentages and uses it to set the take profit;
     * then it sets the stop loss as being under or above the price by a percentage that equals that of the
     * price variation, divided by the REWARD_TO_RISK ratio. Then it calculates the size based on the account balance
     * and the RISK_percentage, and adjusts the leverage accordingly.
     *
     * @param tradingAccount
     * @param graph
     * @param coreData
     * @param tradeOpenCandle
     * @return
     */
    @Override
    public Trade generateOptimalTradeWithDefaultSettings(Account tradingAccount,
                                                         Graph graph,
                                                         CoreData coreData,
                                                         int tradeOpenCandle) {
        return generateOptimalTrade(tradingAccount, graph, coreData, tradeOpenCandle);
    }

    @Override
    public Trade generateOptimalTrade(Account tradingAccount,
                                      Graph graph,
                                      CoreData coreData,
                                      int tradeOpenCandle) {

        Trade trade = null;

        if (tradingInputDataAreLegit(tradingAccount, graph, coreData, tradeOpenCandle)) {

            Triad<Integer, Float, Float> mostProfitableMomentAndPriceVariationAndStopLoss = findMostProfitableMomentAndPriceAndStopLoss(
                    graph,
                    coreData,
                    tradeOpenCandle,
                    -1);

            int mostProfitableMoment = mostProfitableMomentAndPriceVariationAndStopLoss.first();
            float mostProfitablePriceVariation = filterPriceVariation(mostProfitableMomentAndPriceVariationAndStopLoss.second());

            if (mostProfitablePriceVariation == 0) {
                return Trade.blank();
            }
            boolean side = mostProfitablePriceVariation > 0;

            float openingPrice = Format.roundTwoDigits(getCandleClosePrice(graph, tradeOpenCandle));

            Triad<Float, Float, Double> tpAndSlAndSize = defineTpAndSlAndSize(tradingAccount.getBalance(), openingPrice, mostProfitablePriceVariation);

            if (tpAndSlAndSize.second() == openingPrice || tpAndSlAndSize.first() == openingPrice) {
                return Trade.blank();
            }

            int maxScope = 0;
            Optional<PatternBox> tradingPatternBox = coreData.getTradingPatternBox(graph.getSymbol(), graph.getTimeframe());
            if (tradingPatternBox.isPresent()) {
                maxScope = tradingPatternBox.get().getMaxScope() - 1;
            }

            trade = new Trade(
                    graph.getName(),
                    graph.getTimeframe(),
                    graph.getSymbol(),
                    tradingAccount,
                    graph.getFloatCandles().get(tradeOpenCandle).dateTime(),
                    tpAndSlAndSize.third(),
                    graph.getFloatCandles().get(tradeOpenCandle).dateTime().plusSeconds(maxScope * graph.getTimeframe().durationInSeconds),
                    side,
                    openingPrice,
                    tpAndSlAndSize.first(),
                    tpAndSlAndSize.second(),
                    tradingProperties.feePercentage(),
                    false
            );

            if (trade.isUnfunded()) {
                log.warn("Could not open trade: not enough funds (account balance: {})", tradingAccount.getBalance());
            }
        }
        return trade;
    }

    private boolean tradingInputDataAreLegit(Account tradingAccount, Graph graph, CoreData coreData, int tradeOpenCandle) {
        return graph != null
                && coreData != null
                && Check.isNotEmpty(coreData.getTradingPatternBoxes())
                && tradeOpenCandle >= 0
                && graph.getFloatCandles().size() > tradeOpenCandle
                && graph.getFloatCandles().get(tradeOpenCandle) != null
                && tradingAccount != null
                && tradingAccount.getBalance() > 0;
    }

    @Contract("_, _, _, _ -> new")
    private @NonNull Triad<Integer, Float, Float> findMostProfitableMomentAndPriceAndStopLoss(@NonNull Graph graph, @NonNull CoreData coreData, int tradeOpenCandle, int maxDuration) {

        int mostProfitableMoment = 0;
        float mostProfitablePrice = 0;
        float stopLoss = 0;
        float highestPrice = 0;
        float lowestPrice = 0;
        int lowestPriceMoment = 0;
        int highestPriceMoment = 0;

        PatternBox patternBox = graph.getFirstMatchingChartObjectIn(coreData.getTradingPatternBoxes());

        if (patternBox != null && Check.isNotEmpty(patternBox.getPatterns())) {

            for (Map.Entry<Integer, List<Pattern>> entry : patternBox.getPatterns().entrySet()) {

                float priceVariationForScope = predictPriceVariationForScope(graph, patternBox, tradeOpenCandle, entry.getKey());

                if (highestPrice == 0) {
                    highestPrice = priceVariationForScope;
                    highestPriceMoment = entry.getKey();
                }

                if (lowestPrice == 0) {
                    lowestPrice = priceVariationForScope;
                    lowestPriceMoment = entry.getKey();
                }

                if (entry.getKey() < maxDuration || maxDuration == -1) {

                    if (priceVariationForScope > highestPrice) {
                        highestPrice = priceVariationForScope;
                        highestPriceMoment = entry.getKey();
                    }

                    if (priceVariationForScope < lowestPrice) {
                        lowestPrice = priceVariationForScope;
                        lowestPriceMoment = entry.getKey();
                    }
                }
            }
            if (abs(highestPrice) > abs(lowestPrice)) {
                mostProfitablePrice = highestPrice;
                mostProfitableMoment = highestPriceMoment;
                stopLoss = lowestPrice;
            } else {
                mostProfitablePrice = lowestPrice;
                mostProfitableMoment = lowestPriceMoment;
                stopLoss = highestPrice;
            }
        }
        return new Triad<>(mostProfitableMoment, mostProfitablePrice, stopLoss);
    }

    private float filterPriceVariation(float priceVariation) {
        if (abs(priceVariation) < tradingProperties.priceVariationThreshold()) {
            priceVariation = 0;
        }
        return priceVariation;
    }

    private float getCandleClosePrice(Graph graph, int tradeOpenCandle) {

        float mostCurrentPrice = 0;

        if (graph != null
                && tradeOpenCandle >= 0
                && graph.getFloatCandles().size() > tradeOpenCandle
                && graph.getFloatCandles().get(tradeOpenCandle) != null) {
            mostCurrentPrice = graph.getFloatCandles().get(tradeOpenCandle).close();
        }
        return mostCurrentPrice;
    }

    @Contract("_, _, _, _ -> new")
    private @NonNull Triad<Float, Float, Double> defineTpAndSlAndSize(double balance, float openingPrice, float priceVariation) {

        float takeProfit;
        float stopLoss;
        double size;

        priceVariation = priceVariation * tradingProperties.priceVariationMultiplier();

        switch (tradingProperties.slTpStrategy()) {
            case NONE -> {
                takeProfit = 0;
                stopLoss = 0;
                size = ((balance * tradingProperties.riskPercentage()) / 100) / openingPrice;
            }
            case SL_NO_TP -> {
                takeProfit = 0;
                stopLoss = Format.roundTwoDigits(openingPrice - (openingPrice * priceVariation / tradingProperties.rewardToRiskRatio()) / 100);
                size = ((balance * tradingProperties.riskPercentage()) / 100) / abs(stopLoss - openingPrice);
            }
            case TP_NO_SL -> {
                takeProfit = Format.roundTwoDigits(openingPrice + (openingPrice * priceVariation) / 100);
                stopLoss = 0;
                size = balance / openingPrice;
            }
            case EQUAL -> {
                takeProfit = Format.roundTwoDigits(openingPrice + (openingPrice * priceVariation) / 100);
                stopLoss = Format.roundTwoDigits(openingPrice - (openingPrice * priceVariation) / 100);
                size = ((balance * tradingProperties.riskPercentage()) / 100) / abs(stopLoss - openingPrice);
            }
            case SL_IS_2X_TP -> {
                takeProfit = Format.roundTwoDigits(openingPrice + (openingPrice * priceVariation) / 100);
                stopLoss = Format.roundTwoDigits(openingPrice - (openingPrice * priceVariation) * 2 / 100);
                size = ((balance * tradingProperties.riskPercentage()) / 100) / abs(stopLoss - openingPrice);
            }
            case SL_IS_3X_TP -> {
                takeProfit = Format.roundTwoDigits(openingPrice + (openingPrice * priceVariation) / 100);
                stopLoss = Format.roundTwoDigits(openingPrice - (openingPrice * priceVariation) * 3 / 100);
                size = ((balance * tradingProperties.riskPercentage()) / 100) / abs(stopLoss - openingPrice);
            }
            case BASIC_RR -> {
                takeProfit = Format.roundTwoDigits(openingPrice + (openingPrice * priceVariation) / 100);
                stopLoss = Format.roundTwoDigits(openingPrice - (openingPrice * priceVariation / tradingProperties.rewardToRiskRatio()) / 100);
                size = ((balance * tradingProperties.riskPercentage()) / 100) / abs(stopLoss - openingPrice);
            }
            default -> throw new InvalidParametersException("SL_TP_Strategy is unspecified.");
        }
        return new Triad<>(takeProfit, stopLoss, size);
    }

    private float predictPriceVariationForScope(Graph graph, @NonNull PatternBox patternBox, int tradeOpenCandle, int scope) {

        List<Pattern> patterns = patternBox.getPatterns().get(scope);
        float pricePrediction = 0;

        if (Check.isNotEmpty(patterns)
                && patterns.get(0).getLength() < tradeOpenCandle) {

            float divider = 1;
            List<FloatCandle> floatCandles = graph.getFloatCandles().subList(tradeOpenCandle - patterns.get(0).getLength(), tradeOpenCandle);
            List<IntCandle> intCandles = candleRescaler.rescale(floatCandles, patterns.get(0).getGranularity());

            for (Pattern pattern : patterns) {

                float patternPricePrediction = ((TradingPattern) pattern).getPriceVariationPrediction();

                float price = tradingAnalyzer.filterPriceVariation(patternPricePrediction);

                if (price != 0) {
                    int matchScore = tradingAnalyzer.calculateMatchScore(pattern.getIntCandles(), intCandles);

                    pricePrediction = pricePrediction + price * (matchScore / 100f);
                    divider = divider + matchScore / 100f;
                }
            }
            pricePrediction = pricePrediction / divider;
        }
        return pricePrediction;
    }

    @Override
    public void processTradeOnCompletedCandles(Trade trade, TradingAccount account, List<FloatCandle> candles) {

        if (trade != null
                && account != null
                && Check.isNotEmpty(candles)
                && trade.isOpen()
        ) {
            trade.setExpiry(candles.get(candles.size() - 1).dateTime());

            for (FloatCandle candle : candles) {
                if (trade.isSide()) {
                    completeLongTradeOnLimitsHit(candle, trade, account);
                } else {
                    completeShortTradeOnLimitsHit(candle, trade, account);
                }
                if (!trade.isOpen()) {
                    break;
                }
            }
            if (trade.isOpen()) {
                completeExpiredTrade(candles, trade, account);
            }
            account.addTrade(trade);
        }
    }

    private void completeLongTradeOnLimitsHit(@NonNull FloatCandle candle, @NonNull Trade trade, Account account) {
        if (candle.low() < trade.getStopLoss() && trade.isOpen()) {
            trade.hitStopLoss(candle.dateTime());
            account.debit(trade.getPnL());
        }
        if (candle.high() > trade.getTakeProfit() && trade.isOpen() && trade.getTakeProfit() != 0) {
            trade.hitTakeProfit(candle.dateTime());
            account.credit(trade.getPnL());
        }
    }

    private void completeShortTradeOnLimitsHit(@NonNull FloatCandle candle, @NonNull Trade trade, Account account) {
        if (candle.high() > trade.getStopLoss() && trade.isOpen() && trade.getStopLoss() != 0) {
            trade.hitStopLoss(candle.dateTime());
            account.debit(trade.getPnL());
        }
        if (candle.low() < trade.getTakeProfit() && trade.isOpen()) {
            trade.hitTakeProfit(candle.dateTime());
            account.credit(trade.getPnL());
        }
    }

    private void completeExpiredTrade(@NonNull List<FloatCandle> candles, @NonNull Trade trade, Account account) {
        FloatCandle lastCandle = candles.get(candles.size() - 1);
        trade.expire(lastCandle.dateTime(), lastCandle.close());

        if (trade.getPnL() > 0) {
            account.credit(trade.getPnL());
        } else {
            account.debit(trade.getPnL());
        }
    }
}

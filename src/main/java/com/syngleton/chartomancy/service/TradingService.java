package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.analytics.Analyzer;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.factory.CandleFactory;
import com.syngleton.chartomancy.model.charting.candles.FloatCandle;
import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.patterns.IntPattern;
import com.syngleton.chartomancy.model.charting.patterns.LightTradingPattern;
import com.syngleton.chartomancy.model.charting.patterns.Pattern;
import com.syngleton.chartomancy.model.charting.patterns.PatternBox;
import com.syngleton.chartomancy.model.trading.Trade;
import com.syngleton.chartomancy.model.trading.TradingAccount;
import com.syngleton.chartomancy.util.Check;
import com.syngleton.chartomancy.util.Triad;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

@Log4j2
@Service
public class TradingService {

    private static final int REWARD_TO_RISK_RATIO = 3;
    private static final int RISK_PERCENTAGE = 2;

    private final Analyzer analyzer;
    private final CandleFactory candleFactory;

    @Autowired
    public TradingService(Analyzer analyzer, CandleFactory candleFactory) {
        this.analyzer = analyzer;
        this.candleFactory = candleFactory;
    }


    public Trade generateOptimalBasicTimingBasedTrade(TradingAccount tradingAccount, Graph graph, CoreData coreData, int tradeOpenCandle) {

        Trade trade = null;

/*        if (tradingInputDataAreLegit(tradingAccount, graph, coreData, tradeOpenCandle)) {

            Triad<Integer, Float, Float> mostProfitableMomentAndPriceVariationAndStopLoss = findMostProfitableMomentAndPriceAndStopLoss(
                    graph,
                    coreData,
                    tradeOpenCandle,
                    -1);

            boolean side = mostProfitableMomentAndPriceVariationAndStopLoss.second() > 0;

            trade = new Trade(
                    graph.getName(),
                    graph.getTimeframe(),
                    graph.getSymbol(),
                    tradingAccount,
                    graph.getFloatCandles().get(tradeOpenCandle).dateTime(),
                    graph.getFloatCandles()
                            .get(tradeOpenCandle)
                            .dateTime()
                            .plusSeconds(mostProfitableMomentAndPriceVariationAndStopLoss.first() * graph.getTimeframe().durationInSeconds),
                    side,
                    getCandleClosePrice(graph, tradeOpenCandle),
                    1
            );

            if (trade.getStatus() == TradeStatus.UNFUNDED) {
                log.error("Could not open trade: not enough funds (account balance: {})", tradingAccount.getBalance());
                trade = null;
            }
        }*/
        return trade;
    }

    /**
     * This method finds the best price variation percentages and uses it to se the take profit;
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
    public Trade generateOptimalLeveragedTakeProfitBasedTrade(TradingAccount tradingAccount, Graph graph, CoreData coreData, int tradeOpenCandle) {

        Trade trade = null;

        if (tradingInputDataAreLegit(tradingAccount, graph, coreData, tradeOpenCandle)) {

            Triad<Integer, Float, Float> mostProfitableMomentAndPriceVariationAndStopLoss = findMostProfitableMomentAndPriceAndStopLoss(
                    graph,
                    coreData,
                    tradeOpenCandle,
                    -1);

            int mostProfitableMoment = mostProfitableMomentAndPriceVariationAndStopLoss.first();
            float mostProfitablePriceVariation = mostProfitableMomentAndPriceVariationAndStopLoss.second();

            boolean side = mostProfitablePriceVariation > 0;

            float candleClosePrice = getCandleClosePrice(graph, tradeOpenCandle);
            float takeProfit = candleClosePrice + (candleClosePrice * mostProfitablePriceVariation) / 100;
            float stopLoss = candleClosePrice - (candleClosePrice * mostProfitablePriceVariation / REWARD_TO_RISK_RATIO) / 100;
            double size = ((tradingAccount.getBalance() * RISK_PERCENTAGE) / 100) / abs(stopLoss - candleClosePrice);

            trade = new Trade(
                    graph.getName(),
                    graph.getTimeframe(),
                    graph.getSymbol(),
                    tradingAccount,
                    graph.getFloatCandles().get(tradeOpenCandle).dateTime(),
                    size,
                    graph.getFloatCandles().get(tradeOpenCandle).dateTime().plusSeconds(mostProfitableMoment * graph.getTimeframe().durationInSeconds),
                    side,
                    candleClosePrice,
                    takeProfit,
                    stopLoss
            );

            if (trade.getStatus() == TradeStatus.UNFUNDED) {
                log.error("Could not open trade: not enough funds (account balance: {})", tradingAccount.getBalance());
                trade = null;
            }
        }
        return trade;
    }

    private boolean tradingInputDataAreLegit(TradingAccount tradingAccount, Graph graph, CoreData coreData, int tradeOpenCandle) {
        return graph != null
                && coreData != null
                && Check.notNullNotEmpty(coreData.getTradingPatternBoxes())
                && tradeOpenCandle >= 0
                && graph.getFloatCandles().size() > tradeOpenCandle
                && graph.getFloatCandles().get(tradeOpenCandle) != null
                && tradingAccount != null
                && tradingAccount.getBalance() > 0;
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

    private Triad<Integer, Float, Float> findMostProfitableMomentAndPriceAndStopLoss(Graph graph, CoreData coreData, int tradeOpenCandle, int maxDuration) {

        int mostProfitableMoment = 0;
        float mostProfitablePrice = 0;
        float stopLoss = 0;
        float highestPrice = 0;
        float lowestPrice = 0;
        int lowestPriceMoment = 0;
        int highestPriceMoment = 0;

        PatternBox patternBox = graph.getFirstMatchingChartObjectIn(coreData.getTradingPatternBoxes());

        if (patternBox != null && Check.notNullNotEmpty(patternBox.getPatterns())) {

            for (Map.Entry<Integer, List<Pattern>> entry : patternBox.getPatterns().entrySet()) {

                float priceVariationForScope = predictPriceVariationForScope(graph, patternBox, tradeOpenCandle, entry.getKey());
//TODO Faire une moyenne des pricePrediction de tous les scopes ? Ou plutôt une courbe d'évolution ?

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

    private float predictPriceVariationForScope(Graph graph, PatternBox patternBox, int tradeOpenCandle, int scope) {

        List<Pattern> patterns = patternBox.getPatterns().get(scope);
        float pricePrediction = 0;

        if (Check.notNullNotEmpty(patterns)
                && patterns.get(0).getLength() < tradeOpenCandle) {

            float divider = 1;
            List<FloatCandle> floatCandles = graph.getFloatCandles().subList(tradeOpenCandle - patterns.get(0).getLength(), tradeOpenCandle);
            List<IntCandle> intCandles = candleFactory.streamlineToIntCandles(floatCandles, patterns.get(0).getGranularity());

            for (Pattern pattern : patterns) {

                float patternPricePrediction = ((LightTradingPattern) pattern).getPriceVariationPrediction();
                IntPattern tradingPattern = (IntPattern) pattern;

                float price = analyzer.filterPricePrediction(patternPricePrediction);

                if (price != 0) {
                    int matchScore = analyzer.calculateMatchScore(tradingPattern, intCandles);

                    pricePrediction = pricePrediction + price * (matchScore / 100f);
                    divider = divider + matchScore / 100f;
                }
            }
            pricePrediction = pricePrediction / divider;
        }
        return pricePrediction;
    }


    public void processTradeOnCompletedCandles(Trade trade, TradingAccount account, List<FloatCandle> candles) {

        if (
                trade != null
                        && account != null
                        && Check.notNullNotEmpty(candles)
                        && trade.getStatus() == TradeStatus.OPENED
        ) {


            for (FloatCandle candle : candles) {
                if (trade.isSide()) {
                    completeLongTradeOnLimitsHit(candle, trade, account);
                } else {
                    completeShortTradeOnLimitsHit(candle, trade, account);
                }
            }

            if (trade.getStatus() == TradeStatus.OPENED) {
                completeExpiredTrade(candles, trade, account);
            }
        }

    }

    private void completeLongTradeOnLimitsHit(FloatCandle candle, Trade trade, TradingAccount account) {
        if (candle.low() < trade.getStopLoss()) {
            trade.close(candle.dateTime(), trade.getStopLoss(), TradeStatus.STOP_LOSS_HIT);
            account.debit(trade.getMaxLoss());
        }
        if (candle.high() > trade.getTakeProfit()) {
            trade.close(candle.dateTime(), trade.getTakeProfit(), TradeStatus.TAKE_PROFIT_HIT);
            account.credit(trade.getExpectedProfit());
        }
    }

    private void completeShortTradeOnLimitsHit(FloatCandle candle, Trade trade, TradingAccount account) {
        if (candle.high() > trade.getStopLoss()) {
            trade.close(candle.dateTime(), trade.getStopLoss(), TradeStatus.STOP_LOSS_HIT);
            account.debit(trade.getMaxLoss());
        }
        if (candle.low() < trade.getTakeProfit()) {
            trade.close(candle.dateTime(), trade.getTakeProfit(), TradeStatus.TAKE_PROFIT_HIT);
            account.credit(trade.getExpectedProfit());
        }
    }

    private void completeExpiredTrade(List<FloatCandle> candles, Trade trade, TradingAccount account) {
        FloatCandle lastCandle = candles.get(candles.size() - 1);
        trade.close(lastCandle.dateTime(), lastCandle.close(), TradeStatus.EXPIRED);

        if (trade.getPnL() > 0) {
            account.credit(trade.getPnL());
        } else {
            account.debit(trade.getPnL());
        }
    }

}

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

@Log4j2
@Service
public class TradingService {

    private final Analyzer analyzer;
    private final CandleFactory candleFactory;

    @Autowired
    public TradingService(Analyzer analyzer, CandleFactory candleFactory) {
        this.analyzer = analyzer;
        this.candleFactory = candleFactory;
    }


    public Trade generateOptimalBasicTimingBasedTrade(TradingAccount tradingAccount, Graph graph, CoreData coreData, int tradeOpenCandle) {

        Trade trade = null;

        if (tradingInputDataAreLegit(tradingAccount, graph, coreData, tradeOpenCandle)) {

            Triad<Integer, Float, Float> mostProfitableMomentAndPriceVariationAndStopLoss = findMostProfitableMomentAndPriceAndStopLoss(
                    graph,
                    coreData,
                    tradeOpenCandle,
                    -1);

            boolean side = mostProfitableMomentAndPriceVariationAndStopLoss.second() > 0;

            trade = new Trade(graph.getName(),
                    graph.getTimeframe(),
                    graph.getSymbol(),
                    tradingAccount.getBalance(),
                    graph.getFloatCandles().get(tradeOpenCandle).dateTime(),
                    graph.getFloatCandles()
                            .get(tradeOpenCandle)
                            .dateTime()
                            .plusSeconds(mostProfitableMomentAndPriceVariationAndStopLoss.first() * graph.getTimeframe().durationInSeconds),
                    graph.getFloatCandles()
                            .get(tradeOpenCandle)
                            .dateTime()
                            .plusSeconds(mostProfitableMomentAndPriceVariationAndStopLoss.first() * graph.getTimeframe().durationInSeconds),
                    side,
                    getCandleClosePrice(graph, tradeOpenCandle),
                    1);

            if (trade.getStatus() == TradeStatus.UNFUNDED) {
                log.error("Could not open trade: not enough funds (account balance: {})", tradingAccount.getBalance());
                trade = null;
            }
        }
        return trade;
    }

    //TODO Implement this method by calculating the leverage based on the take profit result and the available balance
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
            LocalDateTime tradeOpen = graph.getFloatCandles().get(tradeOpenCandle).dateTime();
            float takeProfit = candleClosePrice + candleClosePrice * mostProfitablePriceVariation / 100;


            trade = new Trade(graph.getName(),
                    graph.getTimeframe(),
                    graph.getSymbol(),
                    tradingAccount.getBalance(),
                    tradeOpen,
                    graph.getFloatCandles().get(tradeOpenCandle).dateTime().plusSeconds(mostProfitableMoment * graph.getTimeframe().durationInSeconds),
                    tradeOpen.plusSeconds(mostProfitableMoment * graph.getTimeframe().durationInSeconds),
                    side,
                    candleClosePrice,
                    1);

            if (trade.getStatus() == TradeStatus.UNFUNDED) {
                log.error("Could not open trade: not enough funds (account balance: {})", tradingAccount.getBalance());
                trade = null;
            }
        }
        return trade;
    }

    private boolean tradingInputDataAreLegit(TradingAccount tradingAccount, Graph graph, CoreData coreData, int tradeOpenCandle)    {
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


}

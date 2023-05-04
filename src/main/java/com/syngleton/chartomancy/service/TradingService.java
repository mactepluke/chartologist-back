package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.analytics.Analyzer;
import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.factory.CandleFactory;
import com.syngleton.chartomancy.model.charting.candles.FloatCandle;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.patterns.IntPattern;
import com.syngleton.chartomancy.model.charting.patterns.LightTradingPattern;
import com.syngleton.chartomancy.model.charting.patterns.Pattern;
import com.syngleton.chartomancy.model.charting.patterns.PatternBox;
import com.syngleton.chartomancy.model.trading.Trade;
import com.syngleton.chartomancy.util.Check;
import com.syngleton.chartomancy.util.Format;
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

    private final Analyzer analyzer;
    private final CandleFactory candleFactory;

    @Autowired
    public TradingService(Analyzer analyzer, CandleFactory candleFactory) {
        this.analyzer = analyzer;
        this.candleFactory = candleFactory;
    }

    //TODO Harmonize the "most current price", "event horizon" and open date time and price parameters so they are all consistent
    public Trade generateOptimalBasicTrade(Graph graph, CoreData coreData, int eventHorizon, int maxDuration, float safetyMargin) {

        Trade trade = null;

        if (graph != null
                && eventHorizon >= 0
                && graph.getFloatCandles().size() > eventHorizon
                && graph.getFloatCandles().get(eventHorizon) != null) {

            safetyMargin = Format.streamline(safetyMargin, 0, 90);

            Triad<Integer, Float, Float> mostProfitableMomentAndPriceVariationAndStopLoss = findMostProfitableMomentAndPriceAndStopLoss(graph, coreData, eventHorizon, maxDuration);

            int mostProfitableMoment = mostProfitableMomentAndPriceVariationAndStopLoss.first();
            float mostProfitablePriceVariation = mostProfitableMomentAndPriceVariationAndStopLoss.second();
            float stopLossVariation = mostProfitableMomentAndPriceVariationAndStopLoss.third();

            boolean side = mostProfitablePriceVariation > 0;

            float mostCurrentPrice = getMostCurrentPrice(graph, eventHorizon);
            float takeProfit = mostCurrentPrice + mostCurrentPrice * mostProfitablePriceVariation / 100;
            float stopLoss = mostCurrentPrice + mostCurrentPrice * stopLossVariation / 100;

            trade = new Trade(graph.getTimeframe(),
                    graph.getSymbol(),
                    graph.getFloatCandles().get(eventHorizon).dateTime(),
                    graph.getFloatCandles().get(eventHorizon).dateTime().plusSeconds(
                            mostProfitableMoment
                                    * graph.getTimeframe().durationInSeconds),
                    side,
                    mostCurrentPrice,
                    takeProfit,
                    stopLoss,
                    1);
            if (maxDuration != -1) {
                trade.setClose(trade.getOpen().plusSeconds(maxDuration * trade.getTimeframe().durationInSeconds));
            }
        }
        return trade;
    }

    private float getMostCurrentPrice(Graph graph, int eventHorizon) {

        float mostCurrentPrice = 0;

        if (graph != null
                && eventHorizon >= 0
                && graph.getFloatCandles().size() > eventHorizon
                && graph.getFloatCandles().get(eventHorizon) != null) {
            mostCurrentPrice = graph.getFloatCandles().get(eventHorizon).close();
        }
        return mostCurrentPrice;
    }

    private Triad<Integer, Float, Float> findMostProfitableMomentAndPriceAndStopLoss(Graph graph, CoreData coreData, int eventHorizon, int maxDuration) {

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

                float priceVariationForScope = predictPriceVariationForScope(graph, patternBox, eventHorizon, entry.getKey());

                log.debug("SCOPE={}, PRICE PREDICTION={}", entry.getKey(), priceVariationForScope);

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
            if (abs(highestPrice) > abs(lowestPrice))   {
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

    private float predictPriceVariationForScope(Graph graph, PatternBox patternBox, int eventHorizon, int scope) {

        List<Pattern> patterns = patternBox.getPatterns().get(scope);
        float pricePrediction = 0;

        if (Check.notNullNotEmpty(patterns)) {

            float divider = 1;

            for (Pattern pattern : patterns) {

                int patternLength = pattern.getLength();

                if (patternLength < eventHorizon) {

                    float patternPricePrediction = ((LightTradingPattern) pattern).getPriceVariationPrediction();
                    IntPattern tradingPattern = (IntPattern) pattern;

                    List<FloatCandle> floatCandles = graph.getFloatCandles().subList(eventHorizon - patternLength, eventHorizon);

                    int matchScore = analyzer.calculateMatchScoreWithExponentialSmoothing(tradingPattern, candleFactory.streamlineToIntCandles(floatCandles, tradingPattern.getGranularity()));

                    pricePrediction = pricePrediction + patternPricePrediction * (matchScore / 100f);
                    divider = divider + matchScore / 100f;
                }
            }
            pricePrediction = pricePrediction / divider;
        }
        return pricePrediction;
    }


}

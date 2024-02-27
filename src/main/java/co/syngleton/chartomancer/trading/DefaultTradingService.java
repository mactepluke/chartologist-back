package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.core_entities.*;
import co.syngleton.chartomancer.util.Calc;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.abs;

@Log4j2
@Service
@AllArgsConstructor
class DefaultTradingService implements TradingService {
    @Getter
    private final TradingAnalyzer tradingAnalyzer;
    private final CandleRescaler candleRescaler;
    private final TradingAdvisor tradingAdvisor;
    @Getter
    private final TradingProperties tradingProperties;

    @Override
    public Map<CoreDataSettingNames, String> getAnalyzerSettingsSnapshot() {
        return tradingAnalyzer.getSettingsSnapshot();
    }

    @Override
    public Trade generateOptimalTakerTrade(Account tradingAccount, Graph graph, CoreData coreData, int tradeOpenCandle) {

        if (tradingInputDataAreInvalid(tradingAccount, graph, coreData, tradeOpenCandle)) {
            throw new IllegalArgumentException("Trading parameters are invalid.");
        }

        final float expectedPriceVariation = findMostProfitablePriceVariation(graph, coreData, tradeOpenCandle);

        if (expectedPriceVariation == 0) {
            return Trade.blank();
        }

        final float openingPrice = getMostRecentClosePrice(graph, tradeOpenCandle);

        final TradingAdvice advice = tradingAdvisor.getAdvice(tradingAccount.getBalance(), openingPrice, expectedPriceVariation);

        if (advice.stopLoss() == openingPrice || advice.takeProfit() == openingPrice) {
            return Trade.blank();
        }


        int maxScope = coreData.getMaxTradingScope(graph.getSymbol(), graph.getTimeframe()) - 1;

        if (maxScope < 0) {
            maxScope = 0;
        }

        final LocalDateTime expectedClose = graph.getFloatCandles().get(tradeOpenCandle).dateTime().plusSeconds(maxScope * graph.getTimeframe().durationInSeconds);
        final boolean side = expectedPriceVariation > 0;
        final LocalDateTime openDateTime = graph.getFloatCandles().get(tradeOpenCandle).dateTime();

        return Trade.withSettings(
                graph.getName(),
                graph.getTimeframe(),
                graph.getSymbol(),
                tradingAccount,
                advice.size(),
                expectedClose,
                side,
                openingPrice,
                advice.takeProfit(),
                advice.stopLoss(),
                tradingProperties.feePercentage()
        ).open(openDateTime);
    }

    private boolean tradingInputDataAreInvalid(Account tradingAccount, Graph graph, CoreData coreData, int tradeOpenCandle) {
        return graph == null
                || coreData == null
                || coreData.hasInvalidStructure()
                || tradeOpenCandle < 0
                || graph.getFloatCandles().size() <= tradeOpenCandle
                || graph.getFloatCandles().get(tradeOpenCandle) == null
                || tradingAccount == null
                || tradingAccount.getBalance() <= 0;
    }

    private @NonNull Float findMostProfitablePriceVariation(@NonNull Graph graph, @NonNull CoreData coreData, int tradeOpenCandle) {

        Set<Integer> scopes = coreData.getTradingPatternScopes(graph.getSymbol(), graph.getTimeframe());
        List<Float> priceVariations = new ArrayList<>();

        for (int scope : scopes) {
            priceVariations.add(predictPriceVariationForScope(graph, coreData, tradeOpenCandle, scope));
        }
        return filterPriceVariation(getMostExtremeValue(priceVariations));
    }

    private float getMostExtremeValue(@NonNull List<Float> values) {

        values.sort(Float::compareTo);

        if (Check.isEmpty(values)) {
            return 0f;
        }
        return Math.max(abs(values.get(0)), abs(values.get(values.size() - 1)));
    }

    private float filterPriceVariation(float priceVariation) {

        if (abs(priceVariation) < tradingProperties.priceVariationThreshold()) {
            priceVariation = 0;
        }

        priceVariation *= tradingProperties.priceVariationMultiplier();

        return priceVariation;
    }

    private float getMostRecentClosePrice(Graph graph, int tradeOpenCandle) {

        if (graph == null
                || tradeOpenCandle < 0
                || graph.getFloatCandles().size() <= tradeOpenCandle
                || graph.getFloatCandles().get(tradeOpenCandle) == null) {
            return 0;
        }

        return Format.roundTwoDigits(graph.getFloatCandles().get(tradeOpenCandle).close());
    }

    private float predictPriceVariationForScope(@NonNull Graph graph, @NonNull CoreData coreData, int tradeOpenCandle, int scope) {

        final List<Pattern> patterns = coreData.getTradingPatterns(graph.getSymbol(), graph.getTimeframe(), scope);

        if (Check.isEmpty(patterns) || patterns.get(0).getLength() >= tradeOpenCandle) {
            return 0;
        }

        float divider = 1;
        final List<FloatCandle> floatCandles = graph.getFloatCandles().subList(tradeOpenCandle - patterns.get(0).getLength(), tradeOpenCandle);
        final List<IntCandle> intCandles = candleRescaler.rescale(floatCandles, patterns.get(0).getGranularity());

        float pricePrediction = 0;

        for (Pattern pattern : patterns) {

            float patternPricePrediction = ((PredictivePattern) pattern).getPriceVariationPrediction();
            float price = tradingAnalyzer.filterPriceVariation(patternPricePrediction);

            if (price != 0) {

                final int matchScore = tradingAnalyzer.calculateMatchScore(pattern.getIntCandles(), intCandles);

                pricePrediction += Calc.xPercentOfY(matchScore, price);
                divider += matchScore / 100f;
            }
        }
        pricePrediction /= divider;

        return pricePrediction;
    }

    @Override
    public TradingSimulationResult simulateTrades(@NonNull final TradeSimulationStrategy strat, final TradingConditionsChecker checker) {

        while (checker.checkIfConditions()
                .whenAppliedTo(strat.getAccount(), strat.countBlankTrades())
                .withNextCandleAndLimit(strat.getTradeOpenCandle(), strat.getBoundary())
                .andIf(!strat.hasUnfundedTrade())
                .doAllowToContinue(TradingConditionsChecker.Option.LOG_DENIAL_REASON)) {

            strat.setTrade(generateAndProcessTrade(strat));

            strat.setNextOpenCandle();
        }
        return strat.exportResult();
    }

    private Trade generateAndProcessTrade(final TradeSimulationStrategy strat) {

        Trade trade = generateOptimalTakerTrade(
                strat.getAccount(),
                strat.getGraph(),
                strat.getCoreData(),
                strat.getTradeOpenCandle()
        );

        if (trade != null && trade.isOpen()) {

            processTradeOnCompletedCandles(
                    trade,
                    strat.getAccount(),
                    strat.getGraph()
                            .getFloatCandles()
                            .subList(strat.getTradeOpenCandle(), strat.getTradeOpenCandle()
                                    + strat.getCoreData().getMaxTradingScope(strat.getGraph().getSymbol(), strat.getGraph().getTimeframe()))
            );
        }
        return trade;
    }

    private void processTradeOnCompletedCandles(Trade trade, TradingAccount account, List<FloatCandle> candles) {

        checkParamsAreValid(trade, account, candles);

        trade.setExpiry(candles.get(candles.size() - 1).dateTime());

        for (FloatCandle candle : candles) {
            if (trade.isSideLong()) {
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

    private void checkParamsAreValid(Trade trade, TradingAccount account, List<FloatCandle> candles) {

        if (trade == null
                || account == null
                || !Check.isNotEmpty(candles)
                || !trade.isOpen()) {
            throw new IllegalArgumentException("Trading parameters are invalid.");
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
        final FloatCandle lastCandle = candles.get(candles.size() - 1);
        trade.expire(lastCandle.dateTime(), lastCandle.close());

        if (trade.getPnL() > 0) {
            account.credit(trade.getPnL());
        } else {
            account.debit(trade.getPnL());
        }
    }
}
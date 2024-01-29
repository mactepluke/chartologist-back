package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.core_entities.FloatCandle;

import java.util.List;

public interface TradeSimulator {
    void processTradeOnCompletedCandles(Trade trade, TradingAccount account, List<FloatCandle> candles);
}

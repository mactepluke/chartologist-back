package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.shared_domain.FloatCandle;

import java.util.List;

public interface TradeSimulator {
    void processTradeOnCompletedCandles(Trade trade, TradingAccount account, List<FloatCandle> candles);
}

package co.syngleton.chartomancer.api_requesting;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.trading.Trade;

import java.util.Set;

public interface TradeQueryService {
    Trade getCurrentBestTrade(Symbol symbol);

    Set<Trade> getCurrentBestTrades(double accountBalance, Symbol symbol, Set<Timeframe> timeframes);

    Trade getCurrentBestTrade(Symbol symbol, Timeframe timeframe);

    Trade getCurrentBestTrade(double accountBalance, Symbol symbol, Timeframe timeframe);
}

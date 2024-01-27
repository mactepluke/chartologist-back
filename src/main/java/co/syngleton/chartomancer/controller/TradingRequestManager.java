package co.syngleton.chartomancer.controller;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.external_api_requesting.DataRequestingService;
import co.syngleton.chartomancer.shared_constants.CoreDataSettingNames;
import co.syngleton.chartomancer.shared_domain.CoreData;
import co.syngleton.chartomancer.shared_domain.Graph;
import co.syngleton.chartomancer.trading.Trade;
import co.syngleton.chartomancer.trading.TradeGenerator;
import co.syngleton.chartomancer.trading.TradingAccount;
import co.syngleton.chartomancer.trading.TradingProperties;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
@AllArgsConstructor
public class TradingRequestManager {
    private final DataRequestingService dataRequestingService;
    private final TradeGenerator tradeGenerator;
    private final CoreData coreData;
    private TradingProperties tradingProperties;


    public Trade getCurrentBestTrade(Symbol symbol) {

        Set<Timeframe> timeframes = coreData.getTradingTimeframes();

        Set<Trade> trades = getCurrentBestTrades(this.tradingProperties.getDefaultAccountBalance(), symbol, timeframes);

        return determineBestTrade(trades);
    }

    private Trade determineBestTrade(Set<Trade> trades) {

        Trade bestTrade = Trade.blank();

        for (Trade trade : trades) {
            if (trade != null && trade.isNotBlank()) {
                if (bestTrade.isBlank()) {
                    bestTrade = trade;
                } else {
                    bestTrade = bestTrade.getExpectedProfit() > trade.getExpectedProfit() ? bestTrade : trade;
                }
            }
        }
        return bestTrade;
    }

    public Set<Trade> getCurrentBestTrades(double accountBalance, Symbol symbol, Set<Timeframe> timeframes) {
        return timeframes
                .stream()
                .map(timeframe -> getCurrentBestTrade(accountBalance, symbol, timeframe))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Trade getCurrentBestTrade(Symbol symbol, Timeframe timeframe) {
        return getCurrentBestTrade(this.tradingProperties.getDefaultAccountBalance(), symbol, timeframe);
    }

    public Trade getCurrentBestTrade(double accountBalance, Symbol symbol, Timeframe timeframe) {

        Graph graph = dataRequestingService.getLatestPriceHistoryGraphWithCurrentPriceCandle(
                symbol,
                timeframe,
                Integer.parseInt(coreData.getTradingPatternSettings().get(CoreDataSettingNames.PATTERN_LENGTH)));

        TradingAccount tradingAccount = new TradingAccount();
        tradingAccount.credit(accountBalance);

        return tradeGenerator.generateOptimalTrade(tradingAccount,
                graph,
                coreData,
                graph.getFloatCandles().size() - 1);
    }

}

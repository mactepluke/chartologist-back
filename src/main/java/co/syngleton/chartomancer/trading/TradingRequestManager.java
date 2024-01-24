package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.external_api_requesting.DataRequestingService;
import co.syngleton.chartomancer.shared_constants.CoreDataSettingNames;
import co.syngleton.chartomancer.shared_domain.ChartObject;
import co.syngleton.chartomancer.shared_domain.CoreData;
import co.syngleton.chartomancer.shared_domain.Graph;
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
    //private final MailingList mailingList;
    private TradingProperties tradingProperties;

    //TODO Refactor and extract interface. Put methods in TradingService? Create a dedicated interface for methods called by the controller?


    public Trade getCurrentBestTrade(Symbol symbol) {

        Set<Timeframe> timeframes = coreData.getTradingPatternBoxes()
                .stream()
                .map(ChartObject::getTimeframe)
                .collect(Collectors.toUnmodifiableSet());

        Set<Trade> trades = timeframes
                .stream()
                .map(timeframe -> getCurrentBestTrade(this.tradingProperties.getDefaultAccountBalance(), symbol, timeframe))
                .collect(Collectors.toUnmodifiableSet());

        Trade bestTrade = Trade.blank();

        for (Trade trade : trades) {
            if (trade != null && trade.getStatus() != TradeStatus.BLANK) {
                if (bestTrade.getStatus() == TradeStatus.BLANK) {
                    bestTrade = trade;
                } else {
                    bestTrade = bestTrade.getExpectedProfit() > trade.getExpectedProfit() ? bestTrade : trade;
                }
            }
        }
        return bestTrade;
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

    public void subscribeToSignals(String email) {
        //mailingList.getTradingSignalSubscribers().add(email);
    }

}

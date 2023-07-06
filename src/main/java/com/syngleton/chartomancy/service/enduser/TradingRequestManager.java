package com.syngleton.chartomancy.service.enduser;

import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.model.charting.misc.ChartObject;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.model.trading.Trade;
import com.syngleton.chartomancy.model.trading.TradeStatus;
import com.syngleton.chartomancy.model.trading.TradingAccount;
import com.syngleton.chartomancy.model.trading.TradingSettings;
import com.syngleton.chartomancy.service.api.ExternalDataSourceService;
import com.syngleton.chartomancy.service.domain.DataService;
import com.syngleton.chartomancy.service.domain.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

@Service
public class TradingRequestManager {

    private final TradingService tradingService;
    private final DataService dataService;
    private final CoreData coreData;
    private final TradingSettings defaultTradingSettings;
    //private final MailingList mailingList;
    private TradingSettings tradingSettings;

    @Autowired
    public TradingRequestManager(TradingService tradingService,
                                 DataService dataService,
                                 CoreData coreData,
                                 TradingSettings tradingSettings
            /*MailingList mailingList*/) {
        this.tradingService = tradingService;
        this.dataService = dataService;
        this.coreData = coreData;
        this.defaultTradingSettings = tradingSettings;
        this.tradingSettings = defaultTradingSettings;
        //this.mailingList = mailingList;
    }

    public TradingSettings getTradingSettings() {
        return this.tradingSettings;
    }

    public void updateTradingSettings(int rewardToRiskRatio,
                                      int riskPercentage,
                                      float priceVariationThreshold,
                                      int priceVariationMultiplier,
                                      TradingSettings.SL_TP_Strategy slTpStrategy,
                                      double feePercentage) {
        this.tradingSettings = new TradingSettings(
                rewardToRiskRatio,
                riskPercentage,
                priceVariationThreshold,
                priceVariationMultiplier,
                slTpStrategy,
                feePercentage
        );
    }

    public void restoreDefaultTradingSettings() {
        tradingSettings = defaultTradingSettings;
    }

    public void setFeePercentage(double feePercentage) {
        this.tradingSettings.setFeePercentage(abs(feePercentage));
    }

    public void setDefaultAccountBalance(double defaultAccountBalance) {
        this.tradingSettings.setDefaultAccountBalance(abs(defaultAccountBalance));
    }

    public Trade getCurrentBestTrade(Symbol symbol) {

        Set<Timeframe> timeframes = coreData.getTradingPatternBoxes()
                .stream()
                .map(ChartObject::getTimeframe)
                .collect(Collectors.toUnmodifiableSet());

        Set<Trade> trades = timeframes
                .stream()
                .map(timeframe -> getCurrentBestTrade(this.tradingSettings.getDefaultAccountBalance(), symbol, timeframe))
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
        return getCurrentBestTrade(this.tradingSettings.getDefaultAccountBalance(), symbol, timeframe);
    }

    public Trade getCurrentBestTrade(double accountBalance, Symbol symbol, Timeframe timeframe) {

        ExternalDataSourceService dataSourceService = dataService.getExternalDataSourceService();

        Graph graph = dataSourceService.getLatestPriceHistoryGraphWithCurrentPriceCandle(
                symbol,
                timeframe,
                coreData.getTradingPatternSettings().getPatternLength());

        TradingAccount tradingAccount = new TradingAccount();
        tradingAccount.credit(accountBalance);

        return tradingService.generateParameterizedTrade(tradingAccount,
                graph,
                coreData,
                graph.getFloatCandles().size() - 1,
                tradingSettings);
    }

    public void subscribeToSignals(String email) {
        //mailingList.getTradingSignalSubscribers().add(email);
    }

}

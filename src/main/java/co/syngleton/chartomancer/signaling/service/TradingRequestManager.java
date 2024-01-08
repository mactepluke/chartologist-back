package co.syngleton.chartomancer.signaling.service;

import co.syngleton.chartomancer.data.CoreData;
import co.syngleton.chartomancer.data.DataService;
import co.syngleton.chartomancer.domain.ChartObject;
import co.syngleton.chartomancer.domain.Graph;
import co.syngleton.chartomancer.domain.Symbol;
import co.syngleton.chartomancer.domain.Timeframe;
import co.syngleton.chartomancer.signaling.misc.ExternalDataSource;
import co.syngleton.chartomancer.signaling.service.datasource.ExternalDataSourceService;
import co.syngleton.chartomancer.trading.model.Trade;
import co.syngleton.chartomancer.trading.model.TradeStatus;
import co.syngleton.chartomancer.trading.model.TradingAccount;
import co.syngleton.chartomancer.trading.model.TradingSettings;
import co.syngleton.chartomancer.trading.service.TradingService;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

@Service
@Log4j2
public class TradingRequestManager implements ApplicationContextAware {

    private final TradingService tradingService;
    private final DataService dataService;
    private final CoreData coreData;
    private final TradingSettings defaultTradingSettings;
    private final ExternalDataSource externalDataSource;
    //private final MailingList mailingList;
    private ApplicationContext applicationContext;
    private ExternalDataSourceService externalDataSourceService;
    private TradingSettings tradingSettings;

    @Autowired
    public TradingRequestManager(TradingService tradingService,
                                 DataService dataService,
                                 CoreData coreData,
                                 TradingSettings tradingSettings,
                                 @Value("${external_data_source}") ExternalDataSource externalDataSource
            /*MailingList mailingList*/) {
        this.tradingService = tradingService;
        this.dataService = dataService;
        this.coreData = coreData;
        this.defaultTradingSettings = tradingSettings;
        this.tradingSettings = defaultTradingSettings;
        this.externalDataSource = externalDataSource;
        //this.mailingList = mailingList;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        log.debug("Using external data source: {}", externalDataSource);
        this.externalDataSourceService = getExternalDataSourceService(externalDataSource);
    }

    private ExternalDataSourceService getExternalDataSourceService(ExternalDataSource externalDataSource) {
        return applicationContext.getBean(String.valueOf(externalDataSource), ExternalDataSourceService.class);
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

        Graph graph = externalDataSourceService.getLatestPriceHistoryGraphWithCurrentPriceCandle(
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

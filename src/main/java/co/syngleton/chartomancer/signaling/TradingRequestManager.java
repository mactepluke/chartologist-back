package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.shared_constants.CoreDataSettingNames;
import co.syngleton.chartomancer.shared_domain.ChartObject;
import co.syngleton.chartomancer.shared_domain.CoreData;
import co.syngleton.chartomancer.shared_domain.Graph;
import co.syngleton.chartomancer.trading.*;
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

    private final TradeGenerator tradeGenerator;
    private final DataProcessor dataProcessor;
    private final CoreData coreData;
    private final TradingSettings defaultTradingSettings;
    private final ExternalDataSource externalDataSource;
    //private final MailingList mailingList;
    private ApplicationContext applicationContext;
    private ExternalDataSourceService externalDataSourceService;
    private TradingSettings tradingSettings;

    @Autowired
    public TradingRequestManager(TradeGenerator tradeGenerator,
                                 DataProcessor dataProcessor,
                                 CoreData coreData,
                                 TradingSettings tradingSettings,
                                 @Value("${external_data_source}") ExternalDataSource externalDataSource
            /*MailingList mailingList*/) {
        this.tradeGenerator = tradeGenerator;
        this.dataProcessor = dataProcessor;
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
                Integer.parseInt(coreData.getTradingPatternSettings().get(CoreDataSettingNames.PATTERN_LENGTH)));

        TradingAccount tradingAccount = new TradingAccount();
        tradingAccount.credit(accountBalance);

        return tradeGenerator.generateOptimalTrade(tradingAccount,
                graph,
                coreData,
                graph.getFloatCandles().size() - 1,
                tradingSettings);
    }

    public void subscribeToSignals(String email) {
        //mailingList.getTradingSignalSubscribers().add(email);
    }

}

package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.data.DataProcessor;
import co.syngleton.chartomancer.domain.Symbol;
import co.syngleton.chartomancer.domain.Timeframe;
import co.syngleton.chartomancer.trading.Trade;
import co.syngleton.chartomancer.trading.TradeDTO;
import co.syngleton.chartomancer.trading.TradeStatus;
import co.syngleton.chartomancer.trading.TradingAccount;
import co.syngleton.chartomancer.util.datatabletool.DataTableTool;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Set;

@Log4j2
@Configuration
@EnableScheduling
public class EmailingConfig {
    private static final int FOUR_HOUR_RATE = 14400000;
    private static final String SIGNALS_HISTORY_FOLDER = "./signals_history/";
    private static final String SIGNALS_FILE_NAME = "signals";
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String MAIL_SUBJECT = "[CHARTOMANCER] Nouveau trade signalé !";
    private static final String MAIL_FOUR_HOUR_BODY = "Chartomancer vous propose le trade BTC/USD suivant à l'échelle des 4 heures :" + NEW_LINE + NEW_LINE;
    private final TradingRequestManager tradingRequestManager;
    private final EmailingService emailingService;
    private final DataProcessor dataProcessor;
    private final TradingAccount signalsTradingAccount;
    @Value("${enable_email_scheduling:false}")
    private boolean enableEmailScheduling;
    @Value("#{'${default_trading_signal_subscriber_emails}'.split(',')}")
    private Set<String> defaultTradingSignalSubscribers;
    @Value("${signals_account_balance:100}")
    private int signalsAccountBalance;


    @Autowired
    public EmailingConfig(TradingRequestManager tradingRequestManager,
                          EmailingService emailingService,
                          DataProcessor dataProcessor) {
        this.tradingRequestManager = tradingRequestManager;
        this.emailingService = emailingService;
        this.dataProcessor = dataProcessor;
        this.signalsTradingAccount = new TradingAccount();
        this.signalsTradingAccount.credit(signalsAccountBalance);
    }

    @Async
    @Scheduled(fixedRate = FOUR_HOUR_RATE)
    public void sendFourHourTradingSignalEmails() {

        if (enableEmailScheduling) {
            Trade trade = tradingRequestManager.getCurrentBestTrade(signalsAccountBalance, Symbol.BTC_USD, Timeframe.FOUR_HOUR);

            log.debug("Scheduled getCurrentBestTrade triggered – Trade status=: {}", trade.getStatus());

            if (trade.getStatus() == TradeStatus.OPENED) {

                updateSignalsHistory(trade, Timeframe.FOUR_HOUR);

                TradeDTO tradeDTO = new TradeDTO(trade);

                defaultTradingSignalSubscribers.forEach(email ->
                        emailingService.sendEmail(email, MAIL_SUBJECT, MAIL_FOUR_HOUR_BODY + tradeDTO));

            }
        }
    }

    private synchronized void updateSignalsHistory(Trade trade, Timeframe timeframe) {
        this.signalsTradingAccount.getTrades().add(trade);
        DataTableTool.writeDataTableToFile(SIGNALS_HISTORY_FOLDER + SIGNALS_FILE_NAME + "_" + timeframe, signalsTradingAccount);
    }

}

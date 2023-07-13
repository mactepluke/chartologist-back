package com.syngleton.chartomancy.configuration;

import com.syngleton.chartomancy.dto.internal.TradeDTO;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.model.trading.Trade;
import com.syngleton.chartomancy.model.trading.TradeStatus;
import com.syngleton.chartomancy.model.trading.TradingAccount;
import com.syngleton.chartomancy.service.domain.DataService;
import com.syngleton.chartomancy.service.enduser.EmailService;
import com.syngleton.chartomancy.service.enduser.TradingRequestManager;
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
public class MailingConfig {
    private static final int FOUR_HOUR_RATE = 14400000;
    private static final String SIGNALS_HISTORY_FOLDER = "./signals_history/";
    private static final String SIGNALS_FILE_NAME = "signals";
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String MAIL_SUBJECT = "[CHARTOMANCER] Nouveau trade signalé !";
    private static final String MAIL_FOUR_HOUR_BODY = "Chartomancer vous propose le trade BTC/USD suivant à l'échelle des 4 heures :" + NEW_LINE + NEW_LINE;
    private final TradingRequestManager tradingRequestManager;
    private final EmailService emailService;
    private final DataService dataService;
    private final TradingAccount signalsTradingAccount;
    @Value("${enable_email_scheduling:false}")
    private boolean enableEmailScheduling;
    @Value("#{'${default_trading_signal_subscriber_emails}'.split(',')}")
    private Set<String> defaultTradingSignalSubscribers;
    @Value("${signals_account_balance:100}")
    private int signalsAccountBalance;


    @Autowired
    public MailingConfig(TradingRequestManager tradingRequestManager,
                         EmailService emailService,
                         DataService dataService) {
        this.tradingRequestManager = tradingRequestManager;
        this.emailService = emailService;
        this.dataService = dataService;
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
                        emailService.sendEmail(email, MAIL_SUBJECT, MAIL_FOUR_HOUR_BODY + tradeDTO));

            }
        }
    }

    private synchronized void updateSignalsHistory(Trade trade, Timeframe timeframe) {
        this.signalsTradingAccount.getTrades().add(trade);
        dataService.writeCsvFile(SIGNALS_HISTORY_FOLDER + SIGNALS_FILE_NAME + "_" + timeframe, signalsTradingAccount);
    }

}

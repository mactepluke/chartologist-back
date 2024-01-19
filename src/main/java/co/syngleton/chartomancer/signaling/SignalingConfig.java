package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.trading.Trade;
import co.syngleton.chartomancer.trading.TradeStatus;
import co.syngleton.chartomancer.trading.TradingAccount;
import co.syngleton.chartomancer.trading.TradingRequestManager;
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
public class SignalingConfig {
    private static final int FOUR_HOUR_RATE = 14400000;
    private static final String SIGNALS_HISTORY_FOLDER = "./signals_history/";
    private static final String SIGNALS_FILE_NAME = "signals";
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String MAIL_SUBJECT = "[CHARTOMANCER] Nouveau trade signalé !";
    private static final String MAIL_FOUR_HOUR_BODY = "Chartomancer vous propose le trade BTC/USD suivant à l'échelle des 4 heures :" + NEW_LINE + NEW_LINE;
    private final TradingRequestManager tradingRequestManager;
    private final SignalingService signalingService;
    private final TradingAccount signalsTradingAccount;
    @Value("${enable_email_scheduling:false}")
    private boolean enableEmailScheduling;
    @Value("#{'${default_trading_signal_subscriber_emails}'.split(',')}")
    private Set<String> defaultTradingSignalSubscribers;
    @Value("${signals_account_balance:100}")
    private int signalsAccountBalance;


    @Autowired
    public SignalingConfig(TradingRequestManager tradingRequestManager,
                           SignalingService signalingService) {
        this.tradingRequestManager = tradingRequestManager;
        this.signalingService = signalingService;
        this.signalsTradingAccount = new TradingAccount();
        this.signalsTradingAccount.credit(signalsAccountBalance);
    }

    @Async
    @Scheduled(fixedRate = FOUR_HOUR_RATE)
    public void sendFourHourTradingSignals() {

        if (enableEmailScheduling) {
            Trade trade = tradingRequestManager.getCurrentBestTrade(signalsAccountBalance, Symbol.BTC_USD, Timeframe.FOUR_HOUR);

            log.debug("Scheduled getCurrentBestTrade triggered – Trade status=: {}", trade.getStatus());

            if (trade.getStatus() == TradeStatus.OPENED) {

                updateSignalsHistory(trade, Timeframe.FOUR_HOUR);

                TradeSignalDTO tradeSignalDTO = new TradeSignalDTO(trade);

                defaultTradingSignalSubscribers.forEach(email ->
                        signalingService.sendSignal(email, MAIL_SUBJECT, MAIL_FOUR_HOUR_BODY + tradeSignalDTO));

            }
        }
    }

    private synchronized void updateSignalsHistory(Trade trade, Timeframe timeframe) {
        this.signalsTradingAccount.getTrades().add(trade);
        DataTableTool.writeDataTableToFile(SIGNALS_HISTORY_FOLDER + SIGNALS_FILE_NAME + "_" + timeframe, signalsTradingAccount);
    }

}

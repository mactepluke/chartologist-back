package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.trade_requester.PassiveTradeRequester;
import co.syngleton.chartomancer.trading.Trade;
import co.syngleton.chartomancer.trading.TradingAccount;
import co.syngleton.chartomancer.util.csvwritertool.CSVWriter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Log4j2
@Configuration
@EnableScheduling
@AllArgsConstructor
class SignalingConfig {
    private static final int FOUR_HOUR_RATE = 14400000;
    private static final String SIGNALS_HISTORY_FOLDER = "./signals_history/";
    private static final String SIGNALS_FILE_NAME = "signals";
    private static final String NEW_LINE = System.lineSeparator();
    private static final String SUBJECT = "[CHARTOMANCER] Nouveau trade signalé !";
    private static final String BODY = "Chartomancer vous propose le trade BTC/USD suivant :" + NEW_LINE + NEW_LINE;
    private final PassiveTradeRequester tradingRequestManager;
    private final SignalingService signalingService;
    private final SignalingProperties signalingProperties;


    @Scheduled(fixedRate = FOUR_HOUR_RATE)
    private synchronized void sendFourHourSignals() {
        if (signalingProperties.enabled() && signalingProperties.rates().contains(Timeframe.FOUR_HOUR)) {
            sendSignal(Timeframe.FOUR_HOUR);
        }
    }

    private synchronized void sendSignal(Timeframe timeframe) {
        Trade trade = tradingRequestManager.getCurrentBestTrade(signalingProperties.exampleAccountBalance(), Symbol.BTC_USD, timeframe);

        log.debug("Scheduled getCurrentBestTrade triggered – Trade status=: {}", trade.getStatus());

        if (trade.isOpen()) {
            updateSignalsHistory(trade, timeframe);
            signalingService.sendSignal(SUBJECT, BODY + new TradeSignalDTO(trade));
        }
    }

    private synchronized void updateSignalsHistory(Trade trade, Timeframe timeframe) {
        TradingAccount tradingAccount = new TradingAccount();
        tradingAccount.addTrade(trade);
        CSVWriter.writeCSVDataToFile(SIGNALS_HISTORY_FOLDER + SIGNALS_FILE_NAME + "_" + timeframe, tradingAccount);
    }

}

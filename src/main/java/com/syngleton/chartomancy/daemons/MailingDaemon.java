package com.syngleton.chartomancy.daemons;

import com.jsoniter.output.JsonStream;
import com.syngleton.chartomancy.data.MailingList;
import com.syngleton.chartomancy.dto.internal.TradeDTO;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.model.trading.Trade;
import com.syngleton.chartomancy.model.trading.TradeStatus;
import com.syngleton.chartomancy.service.enduser.EmailService;
import com.syngleton.chartomancy.service.enduser.TradingRequestManager;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MailingDaemon implements Runnable {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private final MailingList mailingList;
    private final EmailService emailService;
    private final TradingRequestManager tradingRequestManager;

    public MailingDaemon(MailingList mailingList,
                         TradingRequestManager tradingRequestManager,
                         EmailService emailService) {
        this.mailingList = mailingList;
        this.tradingRequestManager = tradingRequestManager;
        this.emailService = emailService;
    }


    @Override
    public void run() {
        while (true) {
            sendTradingSignalEmails();
            try {
                Thread.sleep(Timeframe.FOUR_HOUR.durationInSeconds * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendTradingSignalEmails() {

        Trade trade = tradingRequestManager.getCurrentBestTrade(100, Symbol.BTC_USD, Timeframe.FOUR_HOUR);

        log.debug("Last monitored trade status: {}", trade.getStatus());

        if (trade.getStatus() == TradeStatus.OPENED) {

            TradeDTO tradeDTO = new TradeDTO(trade);

            String subject = "[CHARTOMANCER] Nouveau trade signalé !";
            String body = "Chartomancer a détecté un pattern propice à ouvrir un trade BTC/USD à l'échelle des 4 heures." + NEW_LINE + NEW_LINE
                    + "Voici le trade conseillé pour un compte de trading crédité de " + 100 + " USD :" + NEW_LINE + NEW_LINE
                    + JsonStream.serialize(tradeDTO);

            mailingList.getTradingSignalSubscribers().forEach(email -> emailService.sendEmail(email, subject, body));
        }

    }

}
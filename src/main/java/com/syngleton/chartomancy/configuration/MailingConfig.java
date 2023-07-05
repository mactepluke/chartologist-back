package com.syngleton.chartomancy.configuration;

import com.syngleton.chartomancy.data.MailingList;
import com.syngleton.chartomancy.service.enduser.EmailService;
import com.syngleton.chartomancy.service.enduser.TradingRequestManager;
import com.syngleton.chartomancy.service.misc.LaunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MailingConfig {

    private final LaunchService launchService;
    private final TradingRequestManager tradingRequestManager;
    private final EmailService emailService;

    @Value("#{'${launch_emailing_daemon:false}'.split(',')}")
    private boolean launchEmailingDaemon;
    @Value("#{'${default_trading_signal_subscriber_emails}'.split(',')}")
    private List<String> defaultTradingSignalSubscribers;

    @Autowired
    public MailingConfig(LaunchService launchService, TradingRequestManager tradingRequestManager, EmailService emailService) {
        this.launchService = launchService;
        this.tradingRequestManager = tradingRequestManager;
        this.emailService = emailService;
    }

    @Bean
    MailingList mailingList() {

        MailingList mailingList = new MailingList();
        mailingList.getTradingSignalSubscribers().addAll(defaultTradingSignalSubscribers);

        if (launchEmailingDaemon) {
            launchService.launchMailingDaemon(mailingList, tradingRequestManager, emailService);
        }
        return mailingList;
    }
}

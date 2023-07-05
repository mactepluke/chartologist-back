package com.syngleton.chartomancy.data;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class MailingList {
    @Getter
    private final Set<String> tradingSignalSubscribers;

    public MailingList() {
        this.tradingSignalSubscribers = new HashSet<>();
    }
}

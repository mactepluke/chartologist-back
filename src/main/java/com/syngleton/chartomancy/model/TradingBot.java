package com.syngleton.chartomancy.model;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Data
public class TradingBot {
    private final List<Pattern> patterns;
    private final List<TradingData> tradingHistory;
    private boolean active;
    private TradingStrategy tradingStrategy;

    public TradingBot(List<Pattern> patterns) {
        this.patterns = patterns;
        this.tradingHistory = new ArrayList<>();
    }

    //TODO Let a dedicated trade history analysis service do the job?
    public int getSharpeRatio()    {
        return 0;
    }

    public float getTotalPnL() {
        return 0;
    }

}


package com.syngleton.chartomancy.model.trading;

import com.syngleton.chartomancy.model.charting.TradingPattern;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Data
public class TradingBot {
    private final List<TradingPattern> patterns;
    private final List<TradesData> tradingHistory;
    private boolean active;
    private TradingStrategy tradingStrategy;

    public TradingBot(List<TradingPattern> patterns) {
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


package com.syngleton.chartomancy.model.trading;

import com.syngleton.chartomancy.analytics.Analyzer;
import com.syngleton.chartomancy.model.charting.patterns.PatternBox;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

@ToString
@Data
public class TradingBot {
    private final Set<PatternBox> tradingPatternBoxes;
    private final Analyzer analyzer;
    private boolean active;
    private TradingStrategy tradingStrategy;

    public TradingBot(Set<PatternBox> tradingPatternBoxes, Analyzer analyzer) {
        this.tradingPatternBoxes = tradingPatternBoxes;
        this.analyzer = analyzer;
    }


    public int getSharpeRatio() {
        return 0;
    }

    public float getTotalPnL() {
        return 0;
    }
}

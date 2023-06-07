package com.syngleton.chartomancy.model.trading;

import lombok.Data;

@Data
public class TradingSettings {

    private final int rewardToRiskRatio;
    private final int riskPercentage;
    private final float priceVariationThreshold;
    private final SL_TP_Strategy slTpStrategy;

    public enum SL_TP_Strategy {
        VOID,
        NONE,
        BASIC_RR,
        EQUAL
    }

    public TradingSettings(int rewardToRiskRatio,
                           int riskPercentage,
                           float priceVariationThreshold,
    SL_TP_Strategy slTpStrategy) {
        this.rewardToRiskRatio = rewardToRiskRatio;
        this.riskPercentage = riskPercentage;
        this.priceVariationThreshold = priceVariationThreshold;
        this.slTpStrategy = slTpStrategy;
    }

}

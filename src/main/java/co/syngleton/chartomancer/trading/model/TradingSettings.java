package co.syngleton.chartomancer.trading.model;

import lombok.Data;

@Data
public class TradingSettings {

    private int rewardToRiskRatio;
    private int riskPercentage;
    private float priceVariationThreshold;
    private float priceVariationMultiplier;
    private SL_TP_Strategy slTpStrategy;
    private double feePercentage;
    private double defaultAccountBalance;

    public TradingSettings(int rewardToRiskRatio,
                           int riskPercentage,
                           float priceVariationThreshold,
                           float priceVariationMultiplier,
                           SL_TP_Strategy slTpStrategy,
                           double feePercentage) {
        this.rewardToRiskRatio = rewardToRiskRatio;
        this.riskPercentage = riskPercentage;
        this.priceVariationThreshold = priceVariationThreshold;
        this.priceVariationMultiplier = priceVariationMultiplier;
        this.slTpStrategy = slTpStrategy;
        this.feePercentage = feePercentage;
        this.defaultAccountBalance = 100;
    }

    public enum SL_TP_Strategy {
        VOID,
        NONE,
        BASIC_RR,
        EQUAL,
        SL_NO_TP,
        TP_NO_SL,
        SL_IS_2X_TP,
        SL_IS_3X_TP
    }

}

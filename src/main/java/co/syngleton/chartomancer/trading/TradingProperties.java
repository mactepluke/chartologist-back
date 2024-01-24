package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Smoothing;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "trading", ignoreUnknownFields = false)
@Data
public class TradingProperties {
    private Smoothing matchScoreSmoothing = Smoothing.NONE;
    private int matchScoreThreshold = 0;
    private int priceVariationThreshold = 0;
    private boolean extrapolatePriceVariation = false;
    private boolean extrapolateMatchScore = false;
    private int rewardToRiskRatio = 0;
    private int riskPercentage = 0;
    private float priceVariationMultiplier = 1;
    private SL_TP_Strategy slTpStrategy = SL_TP_Strategy.VOID;
    private double feePercentage = 0.075;
    private double defaultAccountBalance = 100;
}

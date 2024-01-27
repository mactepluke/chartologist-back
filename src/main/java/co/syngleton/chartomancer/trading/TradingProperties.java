package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Smoothing;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "trading")
@Data
public class TradingProperties {
    private Smoothing matchScoreSmoothing = Smoothing.NONE;
    private int matchScoreThreshold = 30;
    private int priceVariationThreshold = 1;
    private boolean extrapolatePriceVariation = false;
    private boolean extrapolateMatchScore = false;
    private int rewardToRiskRatio = 2;
    private int riskPercentage = 5;
    private float priceVariationMultiplier = 1.2f;
    private SL_TP_Strategy slTpStrategy = SL_TP_Strategy.VOID;
    private double feePercentage = 0.1;
    private double defaultAccountBalance = 100;
}

package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Smoothing;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "trading")
public record TradingProperties(
        @DefaultValue("NONE") Smoothing matchScoreSmoothing,
        @DefaultValue("30") int matchScoreThreshold,
        @DefaultValue("1") int priceVariationThreshold,
        @DefaultValue("false") boolean extrapolatePriceVariation,
        @DefaultValue("false") boolean extrapolateMatchScore,
        @DefaultValue("2") int rewardToRiskRatio,
        @DefaultValue("5") int riskPercentage,
        @DefaultValue("1.2f") float priceVariationMultiplier,
        @DefaultValue("VOID") SL_TP_Strategy slTpStrategy,
        @DefaultValue("0.1") double feePercentage
) {
}


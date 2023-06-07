package com.syngleton.chartomancy.configuration;

import com.syngleton.chartomancy.model.trading.TradingSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TradingConfig {

    private static final int DEFAULT_REWARD_TO_RISK_RATIO = 3;
    private static final int DEFAULT_RISK_PERCENTAGE = 2;
    private static final float DEFAULT_TRADING_PRICE_VARIATION_THRESHOLD = 0;
    private static final TradingSettings.SL_TP_Strategy DEFAULT_SL_TP_STRATEGY = TradingSettings.SL_TP_Strategy.BASIC_RR;

    @Value("${reward_to_risk_ratio:0}")
    private int rewardToRiskRatio;
    @Value("${risk_percentage:0}")
    private int riskPercentage;
    @Value("${trading_price_variation_threshold:0}")
    private float tradingPriceVariationThreshold;
    @Value("${SL_TP_Strategy:VOID}")
    private TradingSettings.SL_TP_Strategy slTpStrategy;

    @Bean
    TradingSettings tradingSettings() {

        rewardToRiskRatio = rewardToRiskRatio <= 0 ? DEFAULT_REWARD_TO_RISK_RATIO : rewardToRiskRatio;
        riskPercentage = riskPercentage <= 0 ? DEFAULT_RISK_PERCENTAGE : riskPercentage;
        tradingPriceVariationThreshold = tradingPriceVariationThreshold <= 0 ? DEFAULT_TRADING_PRICE_VARIATION_THRESHOLD : tradingPriceVariationThreshold;
        slTpStrategy = slTpStrategy == TradingSettings.SL_TP_Strategy.VOID ? DEFAULT_SL_TP_STRATEGY : slTpStrategy;

        return new TradingSettings(
                rewardToRiskRatio,
                riskPercentage,
                tradingPriceVariationThreshold,
                slTpStrategy
        );
    }
}

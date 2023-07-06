package com.syngleton.chartomancy.configuration;

import com.syngleton.chartomancy.analytics.Analyzer;
import com.syngleton.chartomancy.analytics.Smoothing;
import com.syngleton.chartomancy.model.trading.TradingSettings;
import com.syngleton.chartomancy.util.Format;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.Math.abs;

@Configuration
public class TradingConfig {

    private static final int DEFAULT_REWARD_TO_RISK_RATIO = 3;
    private static final int DEFAULT_RISK_PERCENTAGE = 2;
    private static final int DEFAULT_TRADING_PRICE_VARIATION_THRESHOLD = 1;
    private static final TradingSettings.SL_TP_Strategy DEFAULT_SL_TP_STRATEGY = TradingSettings.SL_TP_Strategy.BASIC_RR;
    private static final double MAX_FEE_PERCENTAGE = 5;

    @Value("${reward_to_risk_ratio:0}")
    private int rewardToRiskRatio;
    @Value("${risk_percentage:0}")
    private int riskPercentage;
    @Value("${price_variation_multiplier:1}")
    private int priceVariationMultiplier;
    @Value("${SL_TP_Strategy:VOID}")
    private TradingSettings.SL_TP_Strategy slTpStrategy;
    @Value("${fee_percentage:0.075}")
    private double feePercentage;
    @Value("${trading_match_score_smoothing:NONE}")
    private Smoothing tradingMatchScoreSmoothing;
    @Value("${trading_match_score_threshold:0}")
    private int tradingMatchScoreThreshold;
    @Value("${trading_price_variation_threshold:0}")
    private int tradingPriceVariationThreshold;
    @Value("${trading_extrapolate_price_variation:false}")
    private boolean tradingExtrapolatePriceVariation;
    @Value("${trading_extrapolate_match_score:false}")
    private boolean tradingExtrapolateMatchScore;

    @Bean
    TradingSettings tradingSettings() {

        rewardToRiskRatio = rewardToRiskRatio <= 0 ? DEFAULT_REWARD_TO_RISK_RATIO : rewardToRiskRatio;
        riskPercentage = riskPercentage <= 0 ? DEFAULT_RISK_PERCENTAGE : riskPercentage;
        tradingPriceVariationThreshold = tradingPriceVariationThreshold <= 0 ? DEFAULT_TRADING_PRICE_VARIATION_THRESHOLD : tradingPriceVariationThreshold;
        slTpStrategy = slTpStrategy == TradingSettings.SL_TP_Strategy.VOID ? DEFAULT_SL_TP_STRATEGY : slTpStrategy;
        feePercentage = Format.streamline(feePercentage, 0, MAX_FEE_PERCENTAGE);

        rewardToRiskRatio = Format.streamline(abs(rewardToRiskRatio), 1, 10);
        tradingPriceVariationThreshold = Format.streamline(abs(tradingPriceVariationThreshold), 0, 100);
        riskPercentage = Format.streamline(abs(riskPercentage), 1, 100);
        priceVariationMultiplier = Format.streamline(abs(priceVariationMultiplier), 1, 10);

        return new TradingSettings(
                rewardToRiskRatio,
                riskPercentage,
                tradingPriceVariationThreshold,
                priceVariationMultiplier,
                slTpStrategy,
                feePercentage
        );
    }

    @Bean
    Analyzer tradingAnalyzer() {
        return new Analyzer(tradingMatchScoreSmoothing,
                tradingMatchScoreThreshold,
                tradingPriceVariationThreshold,
                tradingExtrapolatePriceVariation,
                tradingExtrapolateMatchScore);
    }

}

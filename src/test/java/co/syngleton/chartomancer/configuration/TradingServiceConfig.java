package co.syngleton.chartomancer.configuration;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.analytics.Smoothing;
import co.syngleton.chartomancer.trading.TradingSettings;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


@TestConfiguration
public class TradingServiceConfig {

    private static final int REWARD_TO_RISK_RATIO = 2;
    private static final int RISK_PERCENTAGE = 5;
    private static final int TRADING_PRICE_VARIATION_THRESHOLD = 1;
    private static final float PRICE_VARIATION_MULTIPLIER = 1.2F;
    private static final TradingSettings.SL_TP_Strategy SL_TP_STRATEGY = TradingSettings.SL_TP_Strategy.SL_IS_3X_TP;
    private static final double FEE_PERCENTAGE = 0.1;
    private static final Smoothing TRADING_MATCH_SCORE_SMOOTHING = Smoothing.NONE;
    private static final int TRADING_MATCH_SCORE_THRESHOLD = 30;
    private static final boolean TRADING_EXTRAPOLATE_PRICE_VARIATION = false;
    private static final boolean TRADING_EXTRAPOLATE_MATCH_SCORE = false;

    @Bean
    TradingSettings tradingSettings() {

        return new TradingSettings(
                REWARD_TO_RISK_RATIO,
                RISK_PERCENTAGE,
                TRADING_PRICE_VARIATION_THRESHOLD,
                PRICE_VARIATION_MULTIPLIER,
                SL_TP_STRATEGY,
                FEE_PERCENTAGE);
    }

    @Bean
    Analyzer tradingAnalyzer() {
        return Analyzer.getNewInstance(TRADING_MATCH_SCORE_SMOOTHING,
                TRADING_MATCH_SCORE_THRESHOLD,
                TRADING_PRICE_VARIATION_THRESHOLD,
                TRADING_EXTRAPOLATE_PRICE_VARIATION,
                TRADING_EXTRAPOLATE_MATCH_SCORE);
    }
}

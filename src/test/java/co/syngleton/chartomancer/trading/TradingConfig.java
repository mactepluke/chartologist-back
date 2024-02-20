package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Analyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TradingConfig {

    @Autowired
    TradingProperties tradingProperties;

    @Bean
    Analyzer tradingAnalyzer() {
        return Analyzer.getNewInstance(tradingProperties.matchScoreSmoothing(),
                tradingProperties.matchScoreThreshold(),
                tradingProperties.priceVariationThreshold(),
                tradingProperties.extrapolatePriceVariation(),
                tradingProperties.extrapolateMatchScore());
    }

    @Bean
    TradingAdvisor tradingAdvisor() {
        return TradingAdvisor.getNewInstance(tradingProperties.rewardToRiskRatio(),
                tradingProperties.riskPercentage(),
                tradingProperties.slTpStrategy());
    }

}

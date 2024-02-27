package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.analytics.TradingAnalyzer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
@Log4j2
class TradingConfig {
    private final TradingProperties tradingProperties;

    @Bean
    TradingAnalyzer tradingAnalyzer() {
        return Analyzer.getNewInstance(
                tradingProperties.matchScoreSmoothing(),
                tradingProperties.matchScoreThreshold(),
                tradingProperties.priceVariationThreshold(),
                tradingProperties.extrapolatePriceVariation(),
                tradingProperties.extrapolateMatchScore()
        );
    }

    @Bean
    TradingAdvisor tradingAdvisor() {
        return TradingAdvisor.getNewInstance(tradingProperties.rewardToRiskRatio(),
                tradingProperties.riskPercentage(),
                tradingProperties.slTpStrategy());
    }

}

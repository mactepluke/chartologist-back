package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.analytics.Analyzer;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
class TradingConfig {
    private final TradingProperties tradingProperties;

    @Bean
    Analyzer tradingAnalyzer() {
        return Analyzer.getNewInstance(
                tradingProperties.matchScoreSmoothing(),
                tradingProperties.matchScoreThreshold(),
                tradingProperties.priceVariationThreshold(),
                tradingProperties.extrapolatePriceVariation(),
                tradingProperties.extrapolateMatchScore()
        );
    }

}

package co.syngleton.chartomancer.analytics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AnalyzerConfig {

    @Value("${match_score_smoothing:NONE}")
    private Smoothing matchScoreSmoothing;
    @Value("${match_score_threshold:0}")
    private int matchScoreThreshold;
    @Value("${price_variation_threshold:0}")
    private int priceVariationThreshold;
    @Value("${extrapolate_price_variation:false}")
    private boolean extrapolatePriceVariation;
    @Value("${extrapolate_match_score:false}")
    private boolean extrapolateMatchScore;

    @Bean
    Analyzer analyzer() {
        return new DefaultAnalyzer(matchScoreSmoothing,
                matchScoreThreshold,
                priceVariationThreshold,
                extrapolatePriceVariation,
                extrapolateMatchScore);
    }


}

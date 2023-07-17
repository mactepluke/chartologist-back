package co.syngleton.chartomancer.configuration;

import co.syngleton.chartomancer.analytics.Smoothing;
import co.syngleton.chartomancer.analytics.Analyzer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnalyzerConfig {

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
        return new Analyzer(matchScoreSmoothing,
                matchScoreThreshold,
                priceVariationThreshold,
                extrapolatePriceVariation,
                extrapolateMatchScore);
    }


}

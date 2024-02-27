package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.analytics.PatternRecognitionAnalyzer;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
class PatternRecognitionConfig {
    private final PatternRecognitionProperties prp;

    @Bean
    PatternRecognitionAnalyzer analyzer() {
        return Analyzer.getNewInstance(
                prp.matchScoreSmoothing(),
                prp.matchScoreThreshold(),
                prp.priceVariationThreshold(),
                prp.extrapolatePriceVariation(),
                prp.extrapolateMatchScore());
    }


}

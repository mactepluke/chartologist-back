package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.analytics.Analyzer;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
class PatternRecognitionConfig {
    private final PatternRecognitionProperties patternRecognitionProperties;

    @Bean
    Analyzer analyzer() {
        return Analyzer.getNewInstance(
                patternRecognitionProperties.getMatchScoreSmoothing(),
                patternRecognitionProperties.getMatchScoreThreshold(),
                patternRecognitionProperties.getPriceVariationThreshold(),
                patternRecognitionProperties.isExtrapolatePriceVariation(),
                patternRecognitionProperties.isExtrapolateMatchScore());
    }


}

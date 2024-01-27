package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.analytics.Smoothing;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "patternrecognition")
record PatternRecognitionProperties(
        @DefaultValue("NONE") Smoothing matchScoreSmoothing,
        @DefaultValue("30") int matchScoreThreshold,
        @DefaultValue("4") int priceVariationThreshold,
        @DefaultValue("false") boolean extrapolatePriceVariation,
        @DefaultValue("false") boolean extrapolateMatchScore
) {
}

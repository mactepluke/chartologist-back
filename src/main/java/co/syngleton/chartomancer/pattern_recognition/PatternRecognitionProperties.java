package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.analytics.Smoothing;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "patternrecognition", ignoreUnknownFields = false)
@Getter
@Setter
public class PatternRecognitionProperties {
    private Smoothing matchScoreSmoothing = Smoothing.NONE;
    private int matchScoreThreshold = 0;
    private int priceVariationThreshold = 0;
    private boolean extrapolatePriceVariation = false;
    private boolean extrapolateMatchScore = false;
}

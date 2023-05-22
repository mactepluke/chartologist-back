package com.syngleton.chartomancy.configuration;

import com.syngleton.chartomancy.analytics.Analyzer;
import com.syngleton.chartomancy.analytics.Smoothing;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class AnalyzerConfigTest {

    private static final Smoothing MATCH_SCORE_SMOOTHING = Smoothing.LINEAR;
    private static final int MATCH_SCORE_THRESHOLD = 0;
    private static final int PRICE_VARIATION_THRESHOLD = 0;
    private static final boolean EXTRAPOLATE_PRICE_VARIATION = false;
    private static final boolean EXTRAPOLATE_MATCH_SCORE = false;

    @Bean
    Analyzer analyzer() {
        return new Analyzer(MATCH_SCORE_SMOOTHING,
                MATCH_SCORE_THRESHOLD,
                PRICE_VARIATION_THRESHOLD,
                EXTRAPOLATE_PRICE_VARIATION,
                EXTRAPOLATE_MATCH_SCORE);
    }
}

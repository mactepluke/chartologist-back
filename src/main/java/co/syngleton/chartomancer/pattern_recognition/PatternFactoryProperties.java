package co.syngleton.chartomancer.pattern_recognition;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "patternfactory")
record PatternFactoryProperties(
        @DefaultValue("30") int minGranularity,
        @DefaultValue("500") int maxGranularity,
        @DefaultValue("10") int minPatternLength,
        @DefaultValue("50") int maxPatternLength,
        @DefaultValue("100") int defaultGranularity,
        @DefaultValue("15") int defaultPatternLength,
        @DefaultValue("8") int defaultScope,
        @DefaultValue("1") int minScope,
        @DefaultValue("30") int maxScope,
        @DefaultValue("1") int minPatternsPerGraph,
        @DefaultValue("100") int testGranularity,
        @DefaultValue("10") int testPatternLength,
        @DefaultValue("2") int testScope
) {
}

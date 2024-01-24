package co.syngleton.chartomancer.pattern_recognition;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "patternfactory", ignoreUnknownFields = false)
@Getter
@Setter
public class PatternFactoryProperties {
    private int minGranularity = 30;
    private int maxGranularity = 500;
    private int minPatternLength = 10;
    private int maxPatternLength = 50;
    private int defaultGranularity = 100;
    private int defaultPatternLength = 15;
    private int defaultScope = 8;
    private int minScope = 1;
    private int maxScope = 30;
    private int minPatternsPerGraph = 1;
    private int testGranularity = 100;
    private int testPatternLength = 10;
    private int testScope = 2;
}

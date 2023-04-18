package com.syngleton.chartomancy.factory;

import com.syngleton.chartomancy.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.syngleton.chartomancy.util.Format.setIntIfZero;
import static com.syngleton.chartomancy.util.Format.streamlineInt;
import static org.apache.commons.collections4.ListUtils.partition;

@Log4j2
@Component
public class PatternFactory {

    private static final int MIN_GRANULARITY = 10;
    private static final int MAX_GRANULARITY = 1000;
    private static final int MIN_PATTERN_LENGTH = 10;
    private static final int MAX_PATTERN_LENGTH = 250;
    private static final int DEFAULT_GRANULARITY = 100;
    private static final int DEFAULT_PATTERN_LENGTH = 50;
    private static final int MIN_PATTERNS_PER_GRAPH = 10;
    private static final int TEST_GRANULARITY = 50;
    private static final int TEST_PATTERN_LENGTH = 20;
    private static final int TEST_SCOPE = 2;
    private static final int TEST_MIN_PATTERN_PER_GRAPH = 4;
    private static final int DEFAULT_SCOPE = 60;
    private static final int MIN_SCOPE = 1;
    private static final int MAX_SCOPE = 250;

    @Value("${min_granularity}")
    private int minGranularity;
    @Value("${max_granularity}")
    private int maxGranularity;
    @Value("${min_pattern_length}")
    private int minPatternLength;
    @Value("${max_pattern_length}")
    private int maxPatternLength;
    @Value("${default_granularity}")
    private int defaultGranularity;
    @Value("${default_pattern_length}")
    private int defaultPatternLength;
    @Value("${min_patterns_per_graph}")
    private int minPatternsPerGraph;
    @Value("${test_granularity}")
    private int testGranularity;
    @Value("${test_pattern_length}")
    private int testPatternLength;
    @Value("${test_scope}")
    private int testScope;
    @Value("${test_min_patterns_per_graph}")
    private int testMinPatternsPerGraph;
    @Value("${default_scope}")
    private int defaultScope;
    @Value("${min_scope}")
    private int minScope;
    @Value("${max_scope}")
    private int maxScope;

    private final CandleFactory candleFactory;

    @Autowired
    public PatternFactory(CandleFactory candleFactory) {
        this.candleFactory = candleFactory;
    }


    public List<Pattern> create(PatternSettings.Builder paramsInput) {
        initializeCheckVariables();

        PatternSettings patternSettings = configParams(paramsInput);

        switch (patternSettings.getPatternType()) {
            case BASIC -> {
                return generateBasicPatterns(patternSettings);
            }
            case PREDICTIVE -> {
                return generatePredictivePatterns(patternSettings);
            }
            default -> {
                log.error("Undefined pattern type.");
                return Collections.emptyList();
            }
        }
    }

    private void initializeCheckVariables() {
        minGranularity = setIntIfZero(minGranularity, MIN_GRANULARITY);
        maxGranularity = setIntIfZero(maxGranularity, MAX_GRANULARITY);
        minPatternLength = setIntIfZero(minPatternLength, MIN_PATTERN_LENGTH);
        maxPatternLength = setIntIfZero(maxPatternLength, MAX_PATTERN_LENGTH);
        minPatternsPerGraph = setIntIfZero(minPatternsPerGraph, MIN_PATTERNS_PER_GRAPH);
        testGranularity = setIntIfZero(testGranularity, TEST_GRANULARITY);
        testPatternLength = setIntIfZero(testPatternLength, TEST_PATTERN_LENGTH);
        testScope = setIntIfZero(testScope, TEST_SCOPE);
        testMinPatternsPerGraph = setIntIfZero(testMinPatternsPerGraph, TEST_MIN_PATTERN_PER_GRAPH);
        defaultScope = setIntIfZero(defaultScope, DEFAULT_SCOPE);
        minScope = setIntIfZero(minScope, MIN_SCOPE);
        maxScope = setIntIfZero(maxScope, MAX_SCOPE);
    }

    private PatternSettings configParams(PatternSettings.Builder paramsInput) {

        PatternSettings initialParams = paramsInput.build();

        switch (initialParams.getAutoconfig()) {
            case NONE -> paramsInput = paramsInput
                    .granularity(streamlineInt(initialParams.getGranularity(), minGranularity, maxGranularity))
                    .length(streamlineInt(initialParams.getLength(), minPatternLength, maxPatternLength))
                    .scope(streamlineInt(initialParams.getScope(), minScope, maxScope));
            case USE_DEFAULTS -> {
                defaultGranularity = defaultGranularity == 0 ?
                        DEFAULT_GRANULARITY : (streamlineInt(defaultGranularity, minGranularity, maxGranularity));

                defaultPatternLength = defaultPatternLength == 0 ?
                        DEFAULT_PATTERN_LENGTH : streamlineInt(defaultPatternLength, minPatternLength, maxPatternLength);

                defaultScope = defaultScope == 0 ?
                        DEFAULT_SCOPE : streamlineInt(defaultScope, minScope, maxScope);

                paramsInput = paramsInput
                        .granularity(defaultGranularity)
                        .length(defaultPatternLength)
                        .scope(defaultScope);
            }
            case MINIMIZE -> paramsInput = paramsInput
                    .length(minPatternLength)
                    .granularity(minGranularity)
                    .scope(minScope);
            case MAXIMIZE -> paramsInput = paramsInput
                    .length(maxPatternLength)
                    .granularity(maxGranularity)
                    .scope(maxScope);
            case BYPASS_SAFETY_CHECK -> log.warn("!! Using raw parameters from input: NOT CHECKED FOR SAFETY !!");
            case TEST -> {
                log.info("Using test parameters for pattern generation (set up in config file)");

                paramsInput = paramsInput
                        .length(streamlineInt(testPatternLength, minPatternLength, maxPatternLength))
                        .granularity(streamlineInt(testGranularity, minPatternLength, maxPatternLength))
                        .scope(streamlineInt(testScope, minScope, maxScope))
                        .name("Test");
                minPatternsPerGraph = testMinPatternsPerGraph;
            }
            default -> log.error("Could not define parameters configuration strategy.");
        }
        return paramsInput.build();
    }

    private List<Pattern> generateBasicPatterns(PatternSettings patternSettings) {

        List<Pattern> patterns = new ArrayList<>();

        if (patternSettings.getGraph() != null
                && patternSettings.getGraph().candles() != null
                && patternSettings.getLength() > 0
                && patternSettings.getGraph().candles().size() / patternSettings.getLength() > minPatternsPerGraph) {
            log.info("Generating basic patterns with parameters: {}", patternSettings.toString());

            List<List<Candle>> graphChunks = partition(patternSettings.getGraph().candles(), patternSettings.getLength());

            int patternCount = 0;

            for (List<Candle> graphChunk : graphChunks) {
                if (graphChunk.size() >= patternSettings.getLength()) {

                    List<PixelatedCandle> pixelatedChunk = candleFactory.pixelateCandles(graphChunk, patternSettings.getGranularity());
                    BasicPattern basicPattern = new BasicPattern();
                    basicPattern.setPixelatedCandles(pixelatedChunk);
                    basicPattern.setGranularity(patternSettings.getGranularity());
                    basicPattern.setLength(patternSettings.getLength());
                    basicPattern.setTimeframe(patternSettings.getGraph().timeframe());
                    basicPattern.setStartDate(graphChunk.get(0).dateTime());
                    basicPattern.setName(patternSettings.getName() + "#" + ++patternCount);
                    basicPattern.setPatternType(PatternType.BASIC);

                    patterns.add(basicPattern);
                }
            }
        } else {
            log.error("Could not use those pattern parameters (or the graph may be empty): {}", patternSettings.toString());
        }
        return patterns;
    }

    private List<Pattern> generatePredictivePatterns(PatternSettings patternSettings) {

        List<Pattern> basicPatterns;
        basicPatterns = generateBasicPatterns(patternSettings);

        return convertFromBasicToPredictivePatterns(basicPatterns, patternSettings);
    }

    private List<Pattern> convertFromBasicToPredictivePatterns(List<Pattern> basicPatterns, PatternSettings patternSettings) {
        List<Pattern> predictivePatterns = new ArrayList<>();

        if (!basicPatterns.isEmpty() && basicPatterns.get(0).getPatternType() == PatternType.BASIC) {
            for (Pattern pattern : basicPatterns) {
                PredictivePattern predictivePattern = new PredictivePattern((BasicPattern) pattern);

                predictivePattern.setPatternType(PatternType.PREDICTIVE);
                predictivePattern.setScope(patternSettings.getScope());

                predictivePatterns.add(predictivePattern);
            }
        }
        return predictivePatterns;
    }

}

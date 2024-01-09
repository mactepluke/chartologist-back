package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.charting.CandleNormalizer;
import co.syngleton.chartomancer.domain.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static co.syngleton.chartomancer.util.Format.setIfZero;
import static co.syngleton.chartomancer.util.Format.streamline;
import static java.lang.Math.round;
import static org.apache.commons.collections4.ListUtils.partition;

@Log4j2
@Component
final class PatternFactory {

    private static final int MIN_GRANULARITY = 30;
    private static final int MAX_GRANULARITY = 500;
    private static final int MIN_PATTERN_LENGTH = 10;
    private static final int MAX_PATTERN_LENGTH = 50;
    private static final int DEFAULT_GRANULARITY = 100;
    private static final int DEFAULT_PATTERN_LENGTH = 15;
    private static final int MIN_PATTERNS_PER_GRAPH = 1;
    private static final int TEST_GRANULARITY = 100;
    private static final int TEST_PATTERN_LENGTH = 10;
    private static final int TEST_SCOPE = 2;
    private static final int TEST_MIN_PATTERN_PER_GRAPH = 1;
    private static final int DEFAULT_SCOPE = 8;
    private static final int MIN_SCOPE = 1;
    private static final int MAX_SCOPE = 30;
    private final CandleNormalizer candleNormalizer;
    @Value("${min_granularity:0}")
    private int minGranularity;
    @Value("${max_granularity:0}")
    private int maxGranularity;
    @Value("${min_pattern_length:0}")
    private int minPatternLength;
    @Value("${max_pattern_length:0}")
    private int maxPatternLength;
    @Value("${default_granularity:0}")
    private int defaultGranularity;
    @Value("${default_pattern_length:0}")
    private int defaultPatternLength;
    @Value("${min_patterns_per_graph:0}")
    private int minPatternsPerGraph;
    @Value("${test_granularity:0}")
    private int testGranularity;
    @Value("${test_pattern_length:0}")
    private int testPatternLength;
    @Value("${test_scope:0}")
    private int testScope;
    @Value("${test_min_patterns_per_graph:0}")
    private int testMinPatternsPerGraph;
    @Value("${default_scope:0}")
    private int defaultScope;
    @Value("${min_scope:0}")
    private int minScope;
    @Value("${max_scope:0}")
    private int maxScope;

    @Autowired
    public PatternFactory(CandleNormalizer candleNormalizer) {
        this.candleNormalizer = candleNormalizer;
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
        minGranularity = setIfZero(minGranularity, MIN_GRANULARITY);
        maxGranularity = setIfZero(maxGranularity, MAX_GRANULARITY);
        minPatternLength = setIfZero(minPatternLength, MIN_PATTERN_LENGTH);
        maxPatternLength = setIfZero(maxPatternLength, MAX_PATTERN_LENGTH);
        minPatternsPerGraph = setIfZero(minPatternsPerGraph, MIN_PATTERNS_PER_GRAPH);
        testGranularity = setIfZero(testGranularity, TEST_GRANULARITY);
        testPatternLength = setIfZero(testPatternLength, TEST_PATTERN_LENGTH);
        testScope = setIfZero(testScope, TEST_SCOPE);
        testMinPatternsPerGraph = setIfZero(testMinPatternsPerGraph, TEST_MIN_PATTERN_PER_GRAPH);
        defaultScope = setIfZero(defaultScope, DEFAULT_SCOPE);
        minScope = setIfZero(minScope, MIN_SCOPE);
        maxScope = setIfZero(maxScope, MAX_SCOPE);
        defaultGranularity = streamline(setIfZero(defaultGranularity, DEFAULT_GRANULARITY), minGranularity, maxGranularity);
        defaultPatternLength = streamline(setIfZero(defaultPatternLength, DEFAULT_PATTERN_LENGTH), minPatternLength, maxPatternLength);
        defaultScope = streamline(setIfZero(defaultScope, DEFAULT_SCOPE), minScope, maxScope);

    }

    private PatternSettings configParams(PatternSettings.Builder paramsInput) {

        PatternSettings initialParams = paramsInput.build();

        switch (initialParams.getAutoconfig()) {
            case NONE -> paramsInput = paramsInput
                    .granularity(streamline(initialParams.getGranularity(), minGranularity, maxGranularity))
                    .length(streamline(initialParams.getLength(), minPatternLength, maxPatternLength))
                    .scope(streamline(initialParams.getScope(), minScope, maxScope));
            case DEFAULT -> paramsInput = paramsInput
                    .length(defaultPatternLength)
                    .granularity(defaultGranularity)
                    .scope(defaultScope);
            case TIMEFRAME -> paramsInput = paramsInput
                    .length(streamline(initialParams.getGraph().getTimeframe().scope * 2, minPatternLength, maxPatternLength))
                    .granularity(defaultGranularity)
                    .scope(streamline(initialParams.getGraph().getTimeframe().scope, minScope, maxScope));
            case TIMEFRAME_LONG -> paramsInput = paramsInput
                    .length(streamline(initialParams.getGraph().getTimeframe().scope * 3, minPatternLength, maxPatternLength))
                    .granularity(defaultGranularity)
                    .scope(streamline(initialParams.getGraph().getTimeframe().scope * 2, minScope, maxScope));
            case TIMEFRAME_VERY_LONG -> paramsInput = paramsInput
                    .length(streamline(initialParams.getGraph().getTimeframe().scope * 6, minPatternLength, maxPatternLength))
                    .granularity(defaultGranularity)
                    .scope(streamline(initialParams.getGraph().getTimeframe().scope * 2, minScope, maxScope));
            case HALF_LENGTH -> paramsInput = paramsInput
                    .length(defaultPatternLength)
                    .granularity(defaultGranularity)
                    .scope(round(defaultPatternLength / (float) 2));
            case EQUAL_LENGTH -> paramsInput = paramsInput
                    .length(defaultPatternLength)
                    .granularity(defaultGranularity)
                    .scope(defaultPatternLength);
            case THIRD_LENGTH -> paramsInput = paramsInput
                    .length(defaultPatternLength)
                    .granularity(defaultGranularity)
                    .scope(round(defaultPatternLength / (float) 3));
            case TWO_THIRDS_LENGTH -> paramsInput = paramsInput
                    .length(defaultPatternLength)
                    .granularity(defaultGranularity)
                    .scope(round(2 * defaultPatternLength / (float) 3));
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
                        .length(streamline(testPatternLength, minPatternLength, maxPatternLength))
                        .granularity(streamline(testGranularity, minPatternLength, maxPatternLength))
                        .scope(streamline(testScope, minScope, maxScope));
                minPatternsPerGraph = testMinPatternsPerGraph;
            }
            default -> log.error("Could not define parameters configuration strategy.");
        }
        return paramsInput.build();
    }

    private List<Pattern> generatePredictivePatterns(PatternSettings patternSettings) {
        List<Pattern> basicPatterns;
        basicPatterns = generateBasicPatterns(patternSettings);

        return convertPatterns(basicPatterns, patternSettings);
    }

    private List<Pattern> generateBasicPatterns(PatternSettings patternSettings) {

        List<Pattern> patterns = new ArrayList<>();

        if (settingsAreValid(patternSettings)) {

            List<List<FloatCandle>> graphChunks = partitionGraph(patternSettings);

            for (List<FloatCandle> graphChunk : graphChunks) {
                if (graphChunk.size() >= patternSettings.getLength()) {

                    List<IntCandle> pixelatedChunk = candleNormalizer.normalizeCandles(graphChunk, patternSettings.getGranularity());

                    BasicPattern basicPattern = new BasicPattern(
                            pixelatedChunk,
                            patternSettings.getGranularity(),
                            patternSettings.getLength(),
                            patternSettings.getGraph().getSymbol(),
                            patternSettings.getGraph().getTimeframe(),
                            graphChunk.get(0).dateTime()
                    );
                    patterns.add(basicPattern);
                }
            }
        } else {
            log.error("Could not use those pattern parameters (or the graph may be empty): {}", patternSettings.toString());
        }

        return patterns;
    }

    private List<List<FloatCandle>> partitionGraph(PatternSettings patternSettings) {

        List<List<FloatCandle>> graphChunks = partition(patternSettings.getGraph().getFloatCandles(), patternSettings.getLength());
        List<List<FloatCandle>> consolidatedChunks = new ArrayList<>(graphChunks);

        if (patternSettings.isAtomicPartition()) {

            for (int i = 0; i < patternSettings.getLength() - 1; i++) {

                List<FloatCandle> shrunkFloatCandles = new ArrayList<>(patternSettings.getGraph().getFloatCandles());

                shrunkFloatCandles.subList(0, i + 1).clear();

                graphChunks = partition(shrunkFloatCandles, patternSettings.getLength());
                consolidatedChunks.addAll(graphChunks);
            }
        }
        return consolidatedChunks;
    }

    private boolean settingsAreValid(PatternSettings patternSettings) {
        return patternSettings.getGraph() != null
                && patternSettings.getGraph().getFloatCandles() != null
                && patternSettings.getLength() > 0
                && patternSettings.getGraph().getFloatCandles().size() / patternSettings.getLength() > minPatternsPerGraph;
    }

    private List<Pattern> convertPatterns(List<Pattern> patterns, PatternSettings patternSettings) {
        List<Pattern> predictivePatterns = new ArrayList<>();

        if (!patterns.isEmpty()) {

            if (patterns.get(0) instanceof BasicPattern) {
                return convertFromBasicToPredictivePatterns(patterns, patternSettings);
            } else {
                log.error("Could not convert patterns because of unrecognized pattern type");
            }
        }
        return predictivePatterns;
    }

    private List<Pattern> convertFromBasicToPredictivePatterns(List<Pattern> basicPatterns, PatternSettings patternSettings) {

        List<Pattern> predictivePatterns = new ArrayList<>();

        for (Pattern pattern : basicPatterns) {
            if (patternSettings.isFullScope()) {
                for (int scope = 1; scope < patternSettings.getScope(); scope++) {
                    PredictivePattern predictivePattern = new PredictivePattern((BasicPattern) pattern, scope);
                    predictivePatterns.add(predictivePattern);
                }
            }
            PredictivePattern predictivePattern = new PredictivePattern((BasicPattern) pattern, patternSettings.getScope());
            predictivePatterns.add(predictivePattern);
        }
        return predictivePatterns;
    }

}

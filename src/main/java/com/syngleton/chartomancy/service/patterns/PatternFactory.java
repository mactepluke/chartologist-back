package com.syngleton.chartomancy.service.patterns;

import com.syngleton.chartomancy.model.data.Candle;
import com.syngleton.chartomancy.model.patterns.BasicPattern;
import com.syngleton.chartomancy.model.patterns.Pattern;
import com.syngleton.chartomancy.model.patterns.PixelatedCandle;
import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.round;
import static org.apache.commons.collections4.ListUtils.partition;

@Log4j2
@Service
public class PatternFactory {

    private static final int MIN_GRANULARITY = 10;
    private static final int MAX_GRANULARITY = 1000;
    private static final int MIN_PATTERN_LENGTH = 10;
    private static final int MAX_PATTERN_LENGTH = 1000;
    private static final int DEFAULT_GRANULARITY = 100;
    private static final int DEFAULT_PATTERN_LENGTH = 50;
    private static final int MIN_PATTERNS_PER_GRAPH = 10;
    private static final int TEST_GRANULARITY = 50;
    private static final int TEST_PATTERN_LENGTH = 20;
    private static final int TEST_MIN_PATTERN_PER_GRAPH = 4;


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
    @Value("${test_min_patterns_per_graph}")
    private int testMinPatternsPerGraph;


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
        minGranularity = Format.setIntIfZero(minGranularity, MIN_GRANULARITY);
        maxGranularity = Format.setIntIfZero(maxGranularity, MAX_GRANULARITY);
        minPatternLength = Format.setIntIfZero(minPatternLength, MIN_PATTERN_LENGTH);
        maxPatternLength = Format.setIntIfZero(maxPatternLength, MAX_PATTERN_LENGTH);
        minPatternsPerGraph = Format.setIntIfZero(minPatternsPerGraph, MIN_PATTERNS_PER_GRAPH);
        testGranularity = Format.setIntIfZero(testGranularity, TEST_GRANULARITY);
        testPatternLength = Format.setIntIfZero(testPatternLength, TEST_PATTERN_LENGTH);
        testMinPatternsPerGraph = Format.setIntIfZero(testMinPatternsPerGraph, TEST_MIN_PATTERN_PER_GRAPH);
    }

    private PatternSettings configParams(PatternSettings.Builder paramsInput) {

        PatternSettings initialParams = paramsInput.build();

        switch (initialParams.getAutoconfig()) {
            case NONE ->
                    paramsInput = paramsInput.granularity(Format.streamlineInt(initialParams.getGranularity(), minGranularity, maxGranularity))
                            .length(Format.streamlineInt(initialParams.getLength(), minPatternLength, maxPatternLength));
            case USE_DEFAULTS -> {
                if (defaultGranularity == 0) {
                    defaultGranularity = DEFAULT_GRANULARITY;
                } else {
                    defaultGranularity = Format.streamlineInt(defaultGranularity, minGranularity, maxGranularity);
                }
                if (defaultPatternLength == 0) {
                    defaultPatternLength = DEFAULT_PATTERN_LENGTH;
                } else {
                    defaultPatternLength = Format.streamlineInt(defaultPatternLength, minPatternLength, maxPatternLength);
                }
                paramsInput = paramsInput.granularity(defaultGranularity)
                        .length(defaultPatternLength);
            }
            case MINIMIZE -> paramsInput = paramsInput.length(minPatternLength).granularity(minGranularity);
            case MAXIMIZE -> paramsInput = paramsInput.length(maxPatternLength).granularity(maxGranularity);
            case BYPASS_SAFETY_CHECK -> log.warn("!! Using raw parameters from input: NOT CHECKED FOR SAFETY !!");
            case TEST -> {
                log.info("Using test parameters for pattern generation (set up in config file)");
                paramsInput = paramsInput.length(Format.streamlineInt(testPatternLength, minPatternLength, maxPatternLength))
                        .granularity(Format.streamlineInt(testGranularity, minPatternLength, maxPatternLength));
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

                    List<PixelatedCandle> pixelatedChunk = pixelateCandles(graphChunk, patternSettings.getGranularity());
                    BasicPattern basicPattern = new BasicPattern();
                    basicPattern.setPixelatedCandles(pixelatedChunk);
                    basicPattern.setGranularity(patternSettings.getGranularity());
                    basicPattern.setLength(patternSettings.getLength());
                    basicPattern.setTimeframe(patternSettings.getGraph().timeframe());
                    basicPattern.setStartDate(graphChunk.get(0).dateTime());
                    basicPattern.setName(patternSettings.getName() + "#" + ++patternCount);

                    patterns.add(basicPattern);
                }
            }
        } else {
            log.error("Could not use those pattern parameters (or the graph may be empty): {}", patternSettings.toString());
        }
        return patterns;
    }

    private List<PixelatedCandle> pixelateCandles(List<Candle> candles, int granularity) {

        float lowest = candles.get(0).low();
        float highest = 0;

        for (Candle candle : candles) {
            lowest = Math.min(lowest, candle.low());
            highest = Math.max(highest, candle.high());
        }

        int divider = round((highest - lowest) / granularity);

        List<PixelatedCandle> pixelatedCandles = new ArrayList<>();


        for (Candle candle : candles) {
            byte[] candlePixels = new byte[granularity];

            int open = round((candle.open() - lowest) / divider);
            int high = round((candle.high() - lowest) / divider);
            int low = round((candle.low() - lowest) / divider);
            int close = round((candle.close() - lowest) / divider);

            open = Format.streamlineInt(open, 0, granularity);
            high = Format.streamlineInt(high, 0, granularity);
            low = Format.streamlineInt(low, 0, granularity);
            close = Format.streamlineInt(close, 0, granularity);


            //Marking an empty pixel
            for (int i = 0; i < low; i++) {
                candlePixels[i] = 0;
            }
            //Marking a wick pixel
            for (int i = low; i < Math.min(open, close); i++) {
                candlePixels[i] = 1;
            }
            //Marking a body pixel
            for (int i = Math.min(open, close); i < Math.max(open, close); i++) {
                candlePixels[i] = 2;
            }
            //Marking a wick pixel
            for (int i = Math.max(open, close); i < high; i++) {
                candlePixels[i] = 1;
            }
            //Marking an empty pixel
            for (int i = high; i < granularity; i++) {
                candlePixels[i] = 0;
            }
            pixelatedCandles.add(new PixelatedCandle(candlePixels, (round(candle.volume() / divider))));
        }
        return pixelatedCandles;
    }

    //TODO Implement predictive patterns generation
    private List<Pattern> generatePredictivePatterns(PatternSettings patternSettings) {

        log.debug("Predictive pattern generation not yet implemented.");
        return new ArrayList<>();
    }
}

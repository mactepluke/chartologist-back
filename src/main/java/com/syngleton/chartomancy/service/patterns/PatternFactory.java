package com.syngleton.chartomancy.service.patterns;

import com.syngleton.chartomancy.model.data.Candle;
import com.syngleton.chartomancy.model.patterns.BasicPattern;
import com.syngleton.chartomancy.model.patterns.CandlePixel;
import com.syngleton.chartomancy.model.patterns.Pattern;
import com.syngleton.chartomancy.model.patterns.PixelatedCandle;
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

    public List<Pattern> create(PatternParams.Builder paramsInput) {

        initializeCheckVariables();

        PatternParams patternParams = configParams(paramsInput);

        switch (patternParams.getPatternType()) {
            case BASIC -> {
                return generateBasicPatterns(patternParams);
            }
            case PREDICTIVE -> {
                return generatePredictivePatterns(patternParams);
            }
            default -> {
                log.error("Undefined pattern type.");
                return Collections.emptyList();
            }
        }
    }

    private void initializeCheckVariables() {
        if (minGranularity == 0) {
            minGranularity = MIN_GRANULARITY;
        }
        if (maxGranularity == 0) {
            maxGranularity = MAX_GRANULARITY;
        }
        if (minPatternLength == 0) {
            minPatternLength = MIN_PATTERN_LENGTH;
        }
        if (maxPatternLength == 0) {
            maxPatternLength = MAX_PATTERN_LENGTH;
        }
        if (minPatternsPerGraph == 0) {
            minPatternsPerGraph = MIN_PATTERNS_PER_GRAPH;
        }
    }

    private PatternParams configParams(PatternParams.Builder paramsInput) {

        PatternParams initialParams = paramsInput.build();

        switch (initialParams.getAutoconfig()) {
            case NONE -> {
                if (initialParams.getGranularity() < minGranularity) {
                    paramsInput = paramsInput.granularity(minGranularity);
                }
                if (initialParams.getGranularity() > maxGranularity) {
                    paramsInput = paramsInput.granularity(maxGranularity);
                }
                if (initialParams.getLength() < minPatternLength) {
                    paramsInput = paramsInput.length(minPatternLength);
                }
                if (initialParams.getLength() > maxPatternLength) {
                    paramsInput = paramsInput.length(maxPatternLength);
                }
            }
            case USE_DEFAULTS -> {
                if (defaultGranularity == 0) {
                    defaultGranularity = DEFAULT_GRANULARITY;
                }
                if (defaultPatternLength == 0) {
                    defaultPatternLength = DEFAULT_PATTERN_LENGTH;
                }
                paramsInput = paramsInput.granularity(defaultGranularity)
                        .length(defaultPatternLength);
            }
            case MINIMIZE -> paramsInput = paramsInput.length(minPatternLength).granularity(minGranularity);
            case MAXIMIZE -> paramsInput = paramsInput.length(maxPatternLength).granularity(maxGranularity);
            case BYPASS_SAFETY_CHECK -> log.warn("!! Using raw parameters from input: NOT CHECKED FOR SAFETY !!");
            default -> log.error("Could not define parameters configuration strategy.");
        }
        return paramsInput.build();
    }


    private List<Pattern> generateBasicPatterns(PatternParams patternParams) {

        List<Pattern> patterns = new ArrayList<>();

        if (patternParams.getGraph() != null
                && patternParams.getGraph().candles() != null
                && patternParams.getLength() > 0
                && patternParams.getGraph().candles().size() / patternParams.getLength() > minPatternsPerGraph) {
            log.info("Generating basic patterns with parameters: {}", patternParams.toString());

            List<List<Candle>> graphChunks = partition(patternParams.getGraph().candles(), patternParams.getLength());

            int patternCount = 0;

            for (List<Candle> graphChunk : graphChunks) {
                if (graphChunk.size() >= patternParams.getLength()) {

                    List<PixelatedCandle> pixelatedChunk = pixelateCandles(graphChunk, patternParams.getGranularity());
                    BasicPattern basicPattern = new BasicPattern();
                    basicPattern.setPixelatedCandles(pixelatedChunk);
                    basicPattern.setGranularity(patternParams.getGranularity());
                    basicPattern.setLength(patternParams.getLength());
                    basicPattern.setTimeframe(patternParams.getGraph().timeframe());
                    basicPattern.setStartDate(graphChunk.get(0).dateTime());
                    basicPattern.setName(patternParams.getName() + "#" + ++patternCount);

                    patterns.add(basicPattern);
                }
            }
        } else {
            log.error("Could not use those pattern parameters (or the graph may be empty): {}", patternParams.toString());
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

        int divider = round( (highest - lowest) / granularity);

        List<PixelatedCandle> pixelatedCandles = new ArrayList<>();

        for (Candle candle : candles) {
            List<CandlePixel> candlePixels = new ArrayList<>();

            int open = round((candle.open() - lowest) / divider);
            int high = round((candle.high() - lowest) / divider);
            int low = round((candle.low() - lowest) / divider);
            int close = round((candle.close() - lowest) / divider);


            for (int i = 0; i < low; i++) {
                candlePixels.add(CandlePixel.EMPTY);
            }
            for (int i = low; i < Math.min(open, close); i++) {
                candlePixels.add(CandlePixel.WICK);
            }
            for (int i = Math.min(open, close); i < Math.max(open, close); i++) {
                candlePixels.add(CandlePixel.BODY);
            }
            for (int i = Math.max(open, close); i < high; i++) {
                candlePixels.add(CandlePixel.WICK);
            }
            for (int i = high; i < granularity; i++) {
                candlePixels.add(CandlePixel.EMPTY);
            }
            pixelatedCandles.add(new PixelatedCandle(candlePixels, (round(candle.volume() / divider))));
        }
        return pixelatedCandles;
    }

    //TODO Implement predictive patterns generation
    private List<Pattern> generatePredictivePatterns(PatternParams patternParams) {

        log.debug("Predictive pattern generation not yet implemented.");
        return new ArrayList<>();
    }
}

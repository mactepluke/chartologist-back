package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.charting.CandleRescaler;
import co.syngleton.chartomancer.shared_domain.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static co.syngleton.chartomancer.util.Format.streamline;
import static java.lang.Math.round;
import static org.apache.commons.collections4.ListUtils.partition;

@Log4j2
@Component
@AllArgsConstructor
final class PatternFactory {
    private final CandleRescaler candleRescaler;
    private final PatternFactoryProperties pfp;

    public List<Pattern> create(PatternSettings.Builder paramsInput) {

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

    private PatternSettings configParams(PatternSettings.Builder paramsInput) {

        PatternSettings initialParams = paramsInput.build();

        switch (initialParams.getAutoconfig()) {
            case NONE -> paramsInput = paramsInput
                    .granularity(streamline(initialParams.getGranularity(), pfp.getMinGranularity(), pfp.getMaxGranularity()))
                    .length(streamline(initialParams.getLength(), pfp.getMinPatternLength(), pfp.getMaxPatternLength()))
                    .scope(streamline(initialParams.getScope(), pfp.getMinScope(), pfp.getMaxScope()));
            case DEFAULT -> paramsInput = paramsInput
                    .length(pfp.getDefaultPatternLength())
                    .granularity(pfp.getDefaultGranularity())
                    .scope(pfp.getDefaultScope());
            case TIMEFRAME -> paramsInput = paramsInput
                    .length(streamline(initialParams.getGraph().getTimeframe().scope * 2, pfp.getMinPatternLength(), pfp.getMaxPatternLength()))
                    .granularity(pfp.getDefaultGranularity())
                    .scope(streamline(initialParams.getGraph().getTimeframe().scope, pfp.getMinScope(), pfp.getMaxScope()));
            case TIMEFRAME_LONG -> paramsInput = paramsInput
                    .length(streamline(initialParams.getGraph().getTimeframe().scope * 3, pfp.getMinPatternLength(), pfp.getMaxPatternLength()))
                    .granularity(pfp.getDefaultGranularity())
                    .scope(streamline(initialParams.getGraph().getTimeframe().scope * 2, pfp.getMinScope(), pfp.getMaxScope()));
            case TIMEFRAME_VERY_LONG -> paramsInput = paramsInput
                    .length(streamline(initialParams.getGraph().getTimeframe().scope * 6, pfp.getMinPatternLength(), pfp.getMaxPatternLength()))
                    .granularity(pfp.getDefaultGranularity())
                    .scope(streamline(initialParams.getGraph().getTimeframe().scope * 2, pfp.getMinScope(), pfp.getMaxScope()));
            case HALF_LENGTH -> paramsInput = paramsInput
                    .length(pfp.getDefaultPatternLength())
                    .granularity(pfp.getDefaultGranularity())
                    .scope(round(pfp.getDefaultPatternLength() / (float) 2));
            case EQUAL_LENGTH -> paramsInput = paramsInput
                    .length(pfp.getDefaultPatternLength())
                    .granularity(pfp.getDefaultGranularity())
                    .scope(pfp.getDefaultPatternLength());
            case THIRD_LENGTH -> paramsInput = paramsInput
                    .length(pfp.getDefaultPatternLength())
                    .granularity(pfp.getDefaultGranularity())
                    .scope(round(pfp.getDefaultPatternLength() / (float) 3));
            case TWO_THIRDS_LENGTH -> paramsInput = paramsInput
                    .length(pfp.getDefaultPatternLength())
                    .granularity(pfp.getDefaultGranularity())
                    .scope(round(2 * pfp.getDefaultPatternLength() / (float) 3));
            case MINIMIZE -> paramsInput = paramsInput
                    .length(pfp.getMinPatternLength())
                    .granularity(pfp.getMinGranularity())
                    .scope(pfp.getMinScope());
            case MAXIMIZE -> paramsInput = paramsInput
                    .length(pfp.getMaxPatternLength())
                    .granularity(pfp.getMaxGranularity())
                    .scope(pfp.getMaxScope());
            case BYPASS_SAFETY_CHECK -> log.warn("!! Using raw parameters from input: NOT CHECKED FOR SAFETY !!");
            case TEST -> {
                log.info("Using test parameters for pattern generation (set up in config file)");

                paramsInput = paramsInput
                        .length(streamline(pfp.getTestPatternLength(), pfp.getMinPatternLength(), pfp.getMaxPatternLength()))
                        .granularity(streamline(pfp.getTestGranularity(), pfp.getMinPatternLength(), pfp.getMaxPatternLength()))
                        .scope(streamline(pfp.getTestScope(), pfp.getMinScope(), pfp.getMaxScope()));
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

                    List<IntCandle> pixelatedChunk = candleRescaler.rescale(graphChunk, patternSettings.getGranularity());

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
                && patternSettings.getGraph().getFloatCandles().size() / patternSettings.getLength() > pfp.getMinPatternsPerGraph();
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

package com.syngleton.chartomancy.service.patterns;

import com.syngleton.chartomancy.model.patterns.Pattern;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class PatternFactory {

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

    public List<Pattern> create(PatternParams patternParams) {

        switch (patternParams.getPatternType()) {
            case PREDICTIVE -> {
                return generatePredictivePatterns(patternParams);
            }
            default -> {
                return generateBasicPatterns(patternParams);
            }
        }
    }

    private List<Pattern> generateBasicPatterns(PatternParams patternParams) {

        if (patternParams.getGraph().candles() != null
                && patternParams.getLength() > 0
                && patternParams.getGraph().candles().size() / patternParams.getLength() > minPatternsPerGraph)   {
            log.info("Generating basic patterns with parameters: {}", patternParams.toString());
        } else {
            throw new InvalidParameterException(patternParams.toString());
        }
        return new ArrayList<>();
    }

    private List<Pattern> generatePredictivePatterns(PatternParams patternParams) {

        return new ArrayList<>();
    }
}

package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.PatternComputer;
import com.syngleton.chartomancy.factory.PatternFactory;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.Graph;
import com.syngleton.chartomancy.model.Pattern;
import com.syngleton.chartomancy.model.PixelatedCandle;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class PatternService {

    private final PatternFactory patternFactory;
    private final PatternComputer patternComputer;
    private static final String NEW_LINE = System.getProperty("line.separator");

    @Autowired
    public PatternService(PatternFactory patternFactory,
                          PatternComputer patternComputer) {
        this.patternFactory = patternFactory;
        this.patternComputer = patternComputer;
    }

    public List<Pattern> create(PatternSettings.Builder settingsInput) {
        return patternFactory.create(settingsInput);
    }

    public List<Pattern> compute(ComputationSettings.Builder settingsInput)  {
        return patternComputer.compute(settingsInput);
    }

    public boolean printPatterns(List<Pattern> patterns) {

        String patternsToPrint = generatePatternsToPrint(patterns);

        if (!patternsToPrint.isEmpty()) {
                log.info(patternsToPrint);
            return true;
        } else {
            log.info("Cannot print patterns: list is empty.");
            return false;
        }
    }

    public String generatePatternsToPrint(List<Pattern> patterns)  {
       StringBuilder patternsBuilder = new StringBuilder();

        if (patterns != null) {
            for (Pattern pattern : patterns) {
                patternsBuilder.append(generatePatternToPrint(pattern));
                patternsBuilder.append(NEW_LINE);

            }
        }
       return patternsBuilder.toString();
    }

    private String generatePatternToPrint(Pattern pattern) {

        StringBuilder patternsBuilder = new StringBuilder();

        patternsBuilder.append(pattern.toString());
        patternsBuilder.append(NEW_LINE);
        patternsBuilder.append(NEW_LINE);

        for (int i = pattern.getGranularity(); i > 0; i--) {

            for (PixelatedCandle pixelatedCandle : pattern.getPixelatedCandles()) {
                String point;
                switch (pixelatedCandle.candle()[i - 1]) {
                    case 1 -> point = "|";
                    case 2 -> point = "H";
                    case 3 -> point = "O";
                    case 4 -> point = "C";
                    default -> point = " ";
                }
                patternsBuilder.append(point);
            }
            patternsBuilder.append(NEW_LINE);
        }
        return patternsBuilder.toString();
    }

    public boolean printPatternsList(List<Pattern> patterns) {
        if (patterns != null) {
            for (Pattern pattern : patterns) {
                log.info(pattern.toString());
            }
            return true;
        } else {
            log.info("Cannot print patterns list: no patterns have been created.");
            return false;
        }
    }
}

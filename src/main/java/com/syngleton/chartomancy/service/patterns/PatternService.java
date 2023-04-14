package com.syngleton.chartomancy.service.patterns;

import com.syngleton.chartomancy.model.patterns.Pattern;
import com.syngleton.chartomancy.model.patterns.PixelatedCandle;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class PatternService {

    private final PatternFactory patternFactory;

    @Autowired
    public PatternService(PatternFactory patternFactory) {
        this.patternFactory = patternFactory;
    }

    public List<Pattern> create(PatternSettings.Builder settingsInput) {
        return patternFactory.create(settingsInput);
    }

    public boolean printPatterns(List<Pattern> patterns) {
        if (patterns != null) {
            for (Pattern pattern : patterns) {
                printPattern(pattern);
            }
            return true;
        } else {
            log.info("Cannot print patterns: no patterns have been created.");
            return false;
        }
    }

    private void printPattern(Pattern pattern) {
        log.info(pattern.toString());

        List<StringBuilder> lines = new ArrayList<>();

        for (int i = pattern.getGranularity(); i > 0; i--) {
            StringBuilder line = new StringBuilder();

            for (PixelatedCandle pixelatedCandle : pattern.getPixelatedCandles()) {
                String point;
                switch (pixelatedCandle.candle()[i - 1]) {
                    case 1 -> point = "|";
                    case 2 -> point = "H";
                    case 3 -> point = "O";
                    case 4 -> point = "C";
                    default -> point = " ";
                }
                line.append(point);
            }
            lines.add(line);
        }
        for (StringBuilder line : lines)    {
            log.info(line.toString());
        }
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

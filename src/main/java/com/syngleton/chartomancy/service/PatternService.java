package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.PatternComputer;
import com.syngleton.chartomancy.factory.PatternFactory;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.charting.Graph;
import com.syngleton.chartomancy.model.charting.Pattern;
import com.syngleton.chartomancy.model.charting.PatternBox;
import com.syngleton.chartomancy.model.charting.PixelatedCandle;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public List<Pattern> createPatterns(PatternSettings.Builder settingsInput) {
        return patternFactory.create(settingsInput);
    }

    public Set<PatternBox> createPatternBoxes(Set<Graph> graphs, PatternSettings.Builder settingsInput) {

        Set<PatternBox> patternBoxes = new HashSet<>();

        if ((graphs != null) && (!graphs.isEmpty())) {
            for (Graph graph : graphs) {
                boolean noMatch = true;

                for (PatternBox patternBox : patternBoxes) {
                    if ((patternBox != null) && (patternBox.matches(graph))) {
                        noMatch = false;
                    }
                }
                if (noMatch) {
                    List<Pattern> patterns = createPatterns(settingsInput.graph(graph));

                    if ((patterns != null) && (!patterns.isEmpty())) {
                        patternBoxes.add(new PatternBox(patterns.get(0).getSymbol(), patterns.get(0).getTimeframe(), patterns));
                    }
                }
            }
        }

        if (patternBoxes.isEmpty()) {
            log.error("Application could not initialize its data: no patterns could be created.");
        } else {
            log.info("Created {} list(s) of patterns", patternBoxes.size());
        }

        return patternBoxes;
    }

    public List<Pattern> computePatterns(ComputationSettings.Builder settingsInput) {
        return patternComputer.compute(settingsInput);
    }

    public Set<PatternBox> computePatternsList(Set<PatternBox> patternBoxes, Set<Graph> graphs, ComputationSettings.Builder settingsInput) {

        Set<PatternBox> computedPatternBoxes = new HashSet<>();

        if (patternBoxes != null
                && !patternBoxes.isEmpty()
                && graphs != null
                && !graphs.isEmpty()
        ) {

            for (PatternBox patternBox : patternBoxes) {

                if ((patternBox != null) && (!patternBox.getPatterns().isEmpty())) {

                    for (Graph graph : graphs) {
                        if (graph.matches(patternBox)) {
                            computedPatternBoxes.add(
                                    new PatternBox(
                                            graph.getSymbol(),
                                            graph.getTimeframe(),
                                            computePatterns(
                                                    settingsInput
                                                            .patterns(patternBox.getPatterns())
                                                            .graph(graph)
                                            )
                                    )
                            );
                        }
                    }

                }
            }
        }

        if (computedPatternBoxes.isEmpty()) {
            log.error("Application could not compute patterns: no pattern has been altered.");
            return patternBoxes;
        }
        log.info("Computed {} list(s) of patterns", computedPatternBoxes.size());

        return computedPatternBoxes;
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

    public String generatePatternsToPrint(List<Pattern> patterns) {
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

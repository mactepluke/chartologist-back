package co.syngleton.chartomancer.service.domain;

import co.syngleton.chartomancer.data.CoreData;
import co.syngleton.chartomancer.data.DataSettings;
import co.syngleton.chartomancer.model.charting.misc.Graph;
import co.syngleton.chartomancer.model.charting.misc.PatternBox;
import co.syngleton.chartomancer.analytics.ComputationSettings;
import co.syngleton.chartomancer.analytics.PatternComputer;
import co.syngleton.chartomancer.factory.PatternFactory;
import co.syngleton.chartomancer.factory.PatternSettings;
import co.syngleton.chartomancer.model.charting.candles.PixelatedCandle;
import co.syngleton.chartomancer.model.charting.patterns.Pattern;
import co.syngleton.chartomancer.model.charting.patterns.pixelated.PixelatedPattern;
import co.syngleton.chartomancer.util.Check;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Log4j2
@Service
public class PatternService {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private final PatternFactory patternFactory;
    private final PatternComputer patternComputer;

    @Autowired
    public PatternService(PatternFactory patternFactory,
                          PatternComputer patternComputer) {
        this.patternFactory = patternFactory;
        this.patternComputer = patternComputer;
    }

    public boolean createPatternBoxes(CoreData coreData, PatternSettings.Builder settingsInput) {

        Set<PatternBox> patternBoxes = new HashSet<>();

        if (coreData != null
                && Check.notNullNotEmpty(coreData.getGraphs())
        ) {
            if (Check.notNullNotEmpty(coreData.getPatternBoxes())) {
                patternBoxes = coreData.getPatternBoxes();
            }
            for (Graph graph : coreData.getGraphs()) {
                if (graph.doesNotMatchAnyChartObjectIn(patternBoxes)) {
                    List<Pattern> patterns = createPatterns(settingsInput.graph(graph));

                    if (Check.notNullNotEmpty(patterns)) {
                        patternBoxes.add(new PatternBox(patterns.get(0), patterns));
                    }
                }
            }
            coreData.setPatternBoxes(patternBoxes);
            updateCoreDataPatternSettings(coreData, settingsInput.build());
        }
        return !patternBoxes.isEmpty();
    }

    public List<Pattern> createPatterns(PatternSettings.Builder settingsInput) {
        return patternFactory.create(settingsInput);
    }

    private void updateCoreDataPatternSettings(@NonNull CoreData coreData, @NonNull PatternSettings patternSettings) {

        DataSettings settings = coreData.getPatternSettings();

        settings.setPatternGranularity(patternSettings.getGranularity());
        settings.setPatternLength(patternSettings.getLength());
        settings.setScope(patternSettings.getScope());
        settings.setFullScope(patternSettings.isFullScope());
        settings.setAtomicPartition(patternSettings.isAtomicPartition());
        settings.setPatternAutoconfig(patternSettings.getAutoconfig());
        settings.setComputationPatternType(patternSettings.getPatternType());
    }

    public boolean computePatternBoxes(CoreData coreData, ComputationSettings.Builder settingsInput) {

        boolean result = false;

        Set<PatternBox> computedPatternBoxes = new HashSet<>();

        if (coreData != null
                && Check.notNullNotEmpty(coreData.getGraphs())
                && Check.notNullNotEmpty(coreData.getPatternBoxes())
        ) {
            for (PatternBox patternBox : coreData.getPatternBoxes()) {

                if ((patternBox != null) && Check.notNullNotEmpty(patternBox.getPatterns())) {

                    Graph matchingGraph = patternBox.getFirstMatchingChartObjectIn(coreData.getGraphs());

                    if (matchingGraph != null) {

                        List<Pattern> computedPatterns = computePatterns(
                                settingsInput
                                        .patterns(patternBox.getListOfAllPatterns())
                                        .graph(matchingGraph)
                        );

                        if (Check.notNullNotEmpty(computedPatterns)) {
                            computedPatternBoxes.add(
                                    new PatternBox(
                                            matchingGraph,
                                            computedPatterns
                                    )
                            );
                        }
                    }
                }
            }
            if (Check.notNullNotEmpty(computedPatternBoxes)) {
                coreData.setPatternBoxes(computedPatternBoxes);
                updateCoreDataComputationSettings(coreData, settingsInput.build());
            }
            result = true;
        }
        return result;
    }

    public List<Pattern> computePatterns(ComputationSettings.Builder settingsInput) {
        return patternComputer.compute(settingsInput);
    }

    private void updateCoreDataComputationSettings(@NonNull CoreData coreData, @NonNull ComputationSettings computationSettings) {
        DataSettings settings = coreData.getPatternSettings();

        settings.setComputationType(computationSettings.getComputationType());
        settings.setComputationAutoconfig(computationSettings.getAutoconfig());
        settings.setExtrapolateMatchScore(patternComputer.getAnalyzer().isExtrapolateMatchScore());
        settings.setExtrapolatePriceVariation(patternComputer.getAnalyzer().isExtrapolatePriceVariation());
        settings.setPriceVariationThreshold(patternComputer.getAnalyzer().getPriceVariationThreshold());
        settings.setMatchScoreThreshold(patternComputer.getAnalyzer().getMatchScoreThreshold());
        settings.setMatchScoreSmoothing(patternComputer.getAnalyzer().getMatchScoreSmoothing());
        settings.setComputationDate(LocalDateTime.now());
    }

    public boolean printPatterns(List<PixelatedPattern> patterns) {

        String patternsToPrint = generatePatternsToPrint(patterns);

        if (!patternsToPrint.isEmpty()) {
            log.info(patternsToPrint);
            return true;
        } else {
            log.info("Cannot print patterns: list is empty.");
            return false;
        }
    }

    public String generatePatternsToPrint(List<PixelatedPattern> patterns) {
        StringBuilder patternsBuilder = new StringBuilder();

        if (patterns != null) {
            for (PixelatedPattern pattern : patterns) {
                patternsBuilder.append(generatePatternToPrint(pattern));
                patternsBuilder.append(NEW_LINE);

            }
        }
        return patternsBuilder.toString();
    }

    private String generatePatternToPrint(PixelatedPattern pattern) {

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

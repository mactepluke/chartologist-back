package co.syngleton.chartomancer.analytics.service;

import co.syngleton.chartomancer.analytics.computation.ComputationSettings;
import co.syngleton.chartomancer.analytics.computation.PatternComputer;
import co.syngleton.chartomancer.analytics.factory.PatternFactory;
import co.syngleton.chartomancer.analytics.factory.PatternSettings;
import co.syngleton.chartomancer.data.CoreData;
import co.syngleton.chartomancer.data.DataSettings;
import co.syngleton.chartomancer.domain.Graph;
import co.syngleton.chartomancer.domain.Pattern;
import co.syngleton.chartomancer.domain.PatternBox;
import co.syngleton.chartomancer.domain.Timeframe;
import co.syngleton.chartomancer.global.tools.Check;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Log4j2
@Service
public class PatternComputingService {

    private final PatternFactory patternFactory;
    private final PatternComputer patternComputer;
    @Value("#{'${patternboxes_timeframes}'.split(',')}")
    private Set<Timeframe> patternBoxesTimeframes;

    @Autowired
    public PatternComputingService(PatternFactory patternFactory,
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

                if (graph.doesNotMatchAnyChartObjectIn(patternBoxes) && patternBoxesTimeframes.contains(graph.getTimeframe())) {

                    log.debug(">>> Creating patterns for graph: " + graph.getTimeframe() + " " + graph.getSymbol());
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
}

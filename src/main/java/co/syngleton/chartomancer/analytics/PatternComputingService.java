package co.syngleton.chartomancer.analytics;

import co.syngleton.chartomancer.data.CommonCoreDataSettingNames;
import co.syngleton.chartomancer.domain.*;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
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
        coreData.setPatternSetting(CommonCoreDataSettingNames.PATTERN_GRANULARITY, Integer.toString(patternSettings.getGranularity()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.PATTERN_LENGTH, Integer.toString(patternSettings.getLength()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.SCOPE, Integer.toString(patternSettings.getScope()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.FULL_SCOPE, Boolean.toString(patternSettings.isFullScope()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.ATOMIC_PARTITION, Boolean.toString(patternSettings.isAtomicPartition()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.PATTERN_AUTOCONFIG, patternSettings.getAutoconfig().toString());
        coreData.setPatternSetting(CommonCoreDataSettingNames.COMPUTATION_PATTERN_TYPE, patternSettings.getPatternType().toString());
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
        coreData.setPatternSetting(CommonCoreDataSettingNames.COMPUTATION_TYPE, computationSettings.getComputationType().toString());
        coreData.setPatternSetting(CommonCoreDataSettingNames.COMPUTATION_AUTOCONFIG, computationSettings.getAutoconfig().toString());
        coreData.setPatternSetting(CommonCoreDataSettingNames.EXTRAPOLATE_MATCH_SCORE, Boolean.toString(patternComputer.getAnalyzer().isExtrapolateMatchScore()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.EXTRAPOLATE_PRICE_VARIATION, Boolean.toString(patternComputer.getAnalyzer().isExtrapolatePriceVariation()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.MATCH_SCORE_THRESHOLD, Double.toString(patternComputer.getAnalyzer().getMatchScoreThreshold()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.PRICE_VARIATION_THRESHOLD, Double.toString(patternComputer.getAnalyzer().getPriceVariationThreshold()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.MATCH_SCORE_SMOOTHING, patternComputer.getAnalyzer().getMatchScoreSmoothing().toString());
        coreData.setPatternSetting(CommonCoreDataSettingNames.COMPUTATION_DATE, Format.toFileNameCompatibleDateTime(LocalDateTime.now()));
    }
}

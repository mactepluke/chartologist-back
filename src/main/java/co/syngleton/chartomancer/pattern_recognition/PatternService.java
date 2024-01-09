package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.analytics.Analyzer;
import co.syngleton.chartomancer.charting.CandleNormalizer;
import co.syngleton.chartomancer.data.CommonCoreDataSettingNames;
import co.syngleton.chartomancer.domain.*;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import co.syngleton.chartomancer.util.Futures;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//TODO Refactor and extract interface
@Log4j2
@Service
final class PatternService implements PatternGenerator, PatternComputer {
    private final PatternFactory patternFactory;
    private final CandleNormalizer candleNormalizer;
    @Value("#{'${patternboxes_timeframes}'.split(',')}")
    private Set<Timeframe> patternBoxesTimeframes;
    @Getter
    @Setter
    private Analyzer analyzer;
    private ComputationSettings computationSettings;
    private ProgressBar pb;

    @Autowired
    public PatternService(PatternFactory patternFactory,
                          CandleNormalizer candleNormalizer,
                          Analyzer analyzer) {
        this.patternFactory = patternFactory;
        this.candleNormalizer = candleNormalizer;
        this.analyzer = analyzer;
    }

    @Override
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

    @Override
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

    @Override
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

    //TODO rentre la méthode private en faisant autrement pour la tester?
    @Override
    public List<Pattern> computePatterns(ComputationSettings.Builder settingsInput) {

        computationSettings = settingsInput.build();

        if (Objects.requireNonNull(computationSettings.getComputationType()) == ComputationType.BASIC_ITERATION) {
            try {
                return computeBasicIterationPatterns();
            } catch (ExecutionException e) {
                e.printStackTrace();
                return Collections.emptyList();
            } catch (InterruptedException e) {
                log.error("Interrupted!", e);
                Thread.currentThread().interrupt();
                return Collections.emptyList();
            }
        }
        log.error("Undefined pattern type.");
        return Collections.emptyList();
    }

    private List<Pattern> computeBasicIterationPatterns() throws ExecutionException, InterruptedException {

        List<Pattern> computedPatterns = new ArrayList<>();

        if (areValid(computationSettings.getPatterns())) {

            startProgressBar();
            computedPatterns = launchMultiThreadedComputations();
            stopProgressBar();

        } else {
            log.error("Could not compute: no computable patterns found.");
        }
        return filterOutUselessPatterns(computedPatterns);
    }

    private boolean areValid(List<Pattern> patterns) {
        return (!patterns.isEmpty() && patterns.get(0) instanceof ComputablePattern);
    }

    private void startProgressBar() {
        pb = new ProgressBar("Processing " + computationSettings.getGraph().getSymbol() + ", " + computationSettings.getGraph().getTimeframe(), computationSettings.getPatterns().size());
        pb.start();
    }

    private void incrementProgressBar() {
        pb.step();
    }

    private void stopProgressBar() {
        pb.stop();
    }

    private List<Pattern> launchMultiThreadedComputations() throws ExecutionException, InterruptedException {

        return Futures.listCompleted(Collections.unmodifiableList(computationSettings.getPatterns()).stream().map(pattern -> CompletableFuture.supplyAsync(() -> computeBasicIterationPattern((ComputablePattern) pattern))).toList());
    }

    private Pattern computeBasicIterationPattern(ComputablePattern pattern) {

        int matchScore;
        float priceVariation;

        if (pattern != null) {

            int computations = computationSettings.getGraph().getFloatCandles().size() - pattern.getLength() - pattern.getScope() + 1;

            float divider = 1;

            for (var i = 0; i < computations; i++) {

                priceVariation = analyzer.calculatePriceVariation(getFollowingFloatCandles(pattern, i), pattern.getScope());
                priceVariation = analyzer.filterPriceVariation(priceVariation);

                if (priceVariation != 0) {

                    List<IntCandle> intCandlesToMatch = candleNormalizer.normalizeCandles(getCandlesToMatch(pattern, i), pattern.getGranularity());
                    matchScore = analyzer.calculateMatchScore(pattern.getIntCandles(), intCandlesToMatch);
                    pattern.setPriceVariationPrediction(pattern.getPriceVariationPrediction() + priceVariation * (matchScore / 100f));
                    divider = incrementDivider(divider, matchScore);
                }
            }
            adjustPriceVariationPrediction(pattern, divider);
        }
        incrementProgressBar();
        return (Pattern) pattern;
    }

    private List<FloatCandle> getFollowingFloatCandles(ComputablePattern pattern, int pivot) {
        return computationSettings.getGraph().getFloatCandles().subList(pivot + pattern.getLength(), pivot + pattern.getLength() + pattern.getScope());
    }

    private List<FloatCandle> getCandlesToMatch(ComputablePattern pattern, int pivot) {
        return computationSettings.getGraph().getFloatCandles().subList(pivot, pivot + pattern.getLength());
    }

    private float incrementDivider(float divider, int matchScore) {
        return divider + matchScore / 100f;
    }

    private void adjustPriceVariationPrediction(ComputablePattern pattern, float divider) {
        pattern.setPriceVariationPrediction(pattern.getPriceVariationPrediction() / divider);
    }

    private List<Pattern> filterOutUselessPatterns(List<Pattern> patterns) {
        return patterns.stream().filter(pattern -> ((ComputablePattern) pattern).getPriceVariationPrediction() != 0).toList();
    }

    private void updateCoreDataComputationSettings(@NonNull CoreData coreData, @NonNull ComputationSettings computationSettings) {
        coreData.setPatternSetting(CommonCoreDataSettingNames.COMPUTATION_TYPE, computationSettings.getComputationType().toString());
        coreData.setPatternSetting(CommonCoreDataSettingNames.COMPUTATION_AUTOCONFIG, computationSettings.getAutoconfig().toString());
        coreData.setPatternSetting(CommonCoreDataSettingNames.EXTRAPOLATE_MATCH_SCORE, Boolean.toString(analyzer.isExtrapolateMatchScore()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.EXTRAPOLATE_PRICE_VARIATION, Boolean.toString(analyzer.isExtrapolatePriceVariation()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.MATCH_SCORE_THRESHOLD, Double.toString(analyzer.getMatchScoreThreshold()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.PRICE_VARIATION_THRESHOLD, Double.toString(analyzer.getPriceVariationThreshold()));
        coreData.setPatternSetting(CommonCoreDataSettingNames.MATCH_SCORE_SMOOTHING, analyzer.getMatchScoreSmoothing().toString());
        coreData.setPatternSetting(CommonCoreDataSettingNames.COMPUTATION_DATE, Format.toFileNameCompatibleDateTime(LocalDateTime.now()));
    }
}

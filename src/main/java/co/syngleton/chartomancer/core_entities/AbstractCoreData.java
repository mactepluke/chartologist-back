package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.util.Check;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

abstract class AbstractCoreData implements CoreData {

    static final String NEW_LINE = System.lineSeparator();
    Set<Graph> graphs;
    Set<PatternBox> patternBoxes;
    Map<CoreDataSettingNames, String> patternSettings;
    Set<PatternBox> tradingPatternBoxes;
    Map<CoreDataSettingNames, String> tradingPatternSettings;

    AbstractCoreData() {
        this.graphs = new HashSet<>();
        this.patternBoxes = new HashSet<>();
        this.patternSettings = new EnumMap<>(CoreDataSettingNames.class);
        this.tradingPatternBoxes = new HashSet<>();
        this.tradingPatternSettings = new EnumMap<>(CoreDataSettingNames.class);
    }

    AbstractCoreData(CoreDataSnapshot coreDataSnapshot) {
        this.graphs = coreDataSnapshot.graphs();
        this.patternBoxes = getPatternBoxesFromSnapshot(coreDataSnapshot.patternBoxes());
        this.patternSettings = coreDataSnapshot.patternSettings();
        this.tradingPatternBoxes = getPatternBoxesFromSnapshot(coreDataSnapshot.tradingPatternBoxes());
        this.tradingPatternSettings = coreDataSnapshot.tradingPatternSettings();
    }

    AbstractCoreData(CoreData coreData) {
        this(coreData.getSnapshot());
    }

    @Override
    public final CoreDataSnapshot getSnapshot() {
        return new CoreDataSnapshot(graphs,
                getPatternBoxesSnapshots(patternBoxes),
                patternSettings,
                getPatternBoxesSnapshots(tradingPatternBoxes),
                tradingPatternSettings);
    }

    private Set<CoreDataSnapshot.PatternBoxSnapShot> getPatternBoxesSnapshots(Set<PatternBox> patternBoxes) {

        Set<CoreDataSnapshot.PatternBoxSnapShot> patternBoxSnapShots = new HashSet<>();

        for (PatternBox patternBox : patternBoxes) {
            patternBoxSnapShots.add(new CoreDataSnapshot.PatternBoxSnapShot(patternBox.getPatterns()));
        }
        return patternBoxSnapShots;
    }

    private Set<PatternBox> getPatternBoxesFromSnapshot(Set<CoreDataSnapshot.PatternBoxSnapShot> patternBoxesSnapShots) {

        Set<PatternBox> restoredPatternBoxes = new HashSet<>();

        for (CoreDataSnapshot.PatternBoxSnapShot patternBoxSnapShot : patternBoxesSnapShots) {

            if (hasValidPatterns(patternBoxSnapShot)) {
                restoredPatternBoxes.add(new PatternBox(patternBoxSnapShot));
            }

        }
        return restoredPatternBoxes;
    }

    private boolean hasValidPatterns(CoreDataSnapshot.PatternBoxSnapShot patternBoxSnapShot) {

        if (patternBoxSnapShot == null || patternBoxSnapShot.patterns() == null) {
            return false;
        }

        for (Map.Entry<Integer, List<Pattern>> entry : patternBoxSnapShot.patterns().entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public final boolean hasInvalidStructure() {
        return this.graphs == null || this.patternBoxes == null || this.patternSettings == null
                || this.tradingPatternBoxes == null || this.tradingPatternSettings == null;
    }

    @Override
    public Set<Graph> getReadOnlyGraphs() {
        return Collections.unmodifiableSet(this.graphs);
    }

    @Override
    public void addGraph(Graph graph) {
        if (graph != null && graph.doesNotMatchAnyChartObjectIn(this.graphs)) {
            this.graphs.add(graph);
        }
    }

    @Override
    public Set<Graph> getUncomputedGraphs() {
        return this.graphs.stream()
                .filter(graph -> graph.doesNotMatchAnyChartObjectIn(patternBoxes))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public synchronized void addPatterns(@NonNull List<Pattern> patterns) {
        if (patterns.isEmpty()) {
            return;
        }
        getPatternBox(patterns.get(0).getSymbol(), patterns.get(0).getTimeframe()).ifPresentOrElse(patternBox -> patternBox.addPatterns(patterns),
                () -> patternBoxes.add(new PatternBox(patterns))
        );
    }

    @Override
    public synchronized void putPatterns(List<Pattern> patterns) {
        if (patterns.isEmpty()) {
            return;
        }
        getPatternBox(patterns.get(0).getSymbol(), patterns.get(0).getTimeframe()).ifPresentOrElse(patternBox -> patternBox.putPatterns(patterns),
                () -> patternBoxes.add(new PatternBox(patterns))
        );
    }

    @Override
    public synchronized int getGraphNumber() {
        if (this.graphs == null) {
            return 0;
        }
        return this.graphs.size();
    }

    @Override
    public synchronized int getNumberOfPatternSets() {
        if (this.graphs == null) {
            return 0;
        }
        return this.patternBoxes.size();
    }

    @Override
    public synchronized int getNumberOfTradingPatternSets() {
        if (this.graphs == null) {
            return 0;
        }
        return this.tradingPatternBoxes.size();
    }

    @Override
    public synchronized boolean pushTradingPatternData() {

        Set<PatternBox> tradingData = new HashSet<>();

        Objects.requireNonNull(this.patternBoxes);

        if (this.patternBoxes.isEmpty()) {
            return false;
        }

        for (PatternBox patternBox : this.patternBoxes) {

            List<Pattern> tradingPatterns = new ArrayList<>();

            if (Check.isNotEmpty(patternBox.getPatterns())) {
                tradingPatterns = convertPatternsToTrading(patternBox.getPatterns());
            }

            if (Check.isNotEmpty(tradingPatterns)) {
                tradingData.add(new PatternBox(tradingPatterns));
            }
        }

        if (tradingData.isEmpty()) {
            return false;
        }

        this.tradingPatternBoxes = tradingData;
        this.tradingPatternSettings.putAll(this.patternSettings);

        return true;
    }

    private List<Pattern> convertPatternsToTrading(Map<Integer, List<Pattern>> patterns) {

        if (patterns == null || patterns.isEmpty()) {
            throw new InvalidParametersException("Cannot convert empty patterns.");
        }

        List<Pattern> tradingPatternsList = new ArrayList<>();

        for (Map.Entry<Integer, List<Pattern>> entry : patterns.entrySet()) {

            for (Pattern pattern : entry.getValue()) {

                if (pattern instanceof ComputablePattern computablePattern) {
                    tradingPatternsList.add(new PredictivePattern(computablePattern));
                }
            }
        }
        return tradingPatternsList;
    }

    public Graph getGraph(Symbol symbol, Timeframe timeframe) {
        for (Graph graph : this.graphs) {
            if (graph.getTimeframe() == timeframe && graph.getSymbol() == symbol) {
                return graph;
            }
        }
        return null;
    }

    private Optional<PatternBox> getPatternBox(Symbol symbol, Timeframe timeframe) {
        return this.patternBoxes.stream().filter(patternBox -> patternBox.getTimeframe() == timeframe
                && patternBox.getSymbol() == symbol).findAny();
    }

    private Optional<PatternBox> getTradingPatternBox(Symbol symbol, Timeframe timeframe) {
        return this.tradingPatternBoxes.stream().filter(patternBox -> patternBox.getTimeframe() == timeframe
                && patternBox.getSymbol() == symbol).findAny();
    }

    @Override
    public <T> void setPatternSetting(CoreDataSettingNames key, T value) {
        this.patternSettings.put(key, String.valueOf(value));
    }

    @Override
    public Set<Timeframe> getTradingTimeframes() {
        return tradingPatternBoxes
                .stream()
                .map(ChartObject::getTimeframe)
                .collect(Collectors.toUnmodifiableSet());
    }


    @Override
    public synchronized List<Pattern> getPatterns() {

        List<Pattern> patterns = new ArrayList<>();

        for (PatternBox patternBox : this.patternBoxes) {
            patterns.addAll(patternBox.getListOfAllPatterns());
        }
        return Collections.unmodifiableList(patterns);
    }

    @Override
    public synchronized List<Pattern> getTradingPatterns() {

        List<Pattern> patterns = new ArrayList<>();

        for (PatternBox patternBox : this.tradingPatternBoxes) {
            patterns.addAll(patternBox.getListOfAllPatterns());
        }
        return Collections.unmodifiableList(patterns);
    }

    @Override
    public synchronized List<Pattern> getPatterns(Symbol symbol, Timeframe timeframe) {
        return Collections.unmodifiableList(Objects.requireNonNull(this.getPatternBox(symbol, timeframe))
                .map(PatternBox::getListOfAllPatterns)
                .orElse(Collections.emptyList()));
    }

    @Override
    public synchronized List<Pattern> getTradingPatterns(Symbol symbol, Timeframe timeframe) {
        return Collections.unmodifiableList(Objects.requireNonNull(this.getTradingPatternBox(symbol, timeframe))
                .map(PatternBox::getListOfAllPatterns)
                .orElse(Collections.emptyList()));
    }

    @Override
    public List<Pattern> getPatterns(Symbol symbol, Timeframe timeframe, int scope) {
        return Collections.unmodifiableList(Objects.requireNonNull(this.getPatternBox(symbol, timeframe)
                .map(patternBox -> patternBox.getPatterns().get(scope))
                .orElse(Collections.emptyList())));
    }

    @Override
    public List<Pattern> getTradingPatterns(Symbol symbol, Timeframe timeframe, int scope) {
        return Collections.unmodifiableList(Objects.requireNonNull(this.getTradingPatternBox(symbol, timeframe)
                .map(patternBox -> patternBox.getPatterns().get(scope))
                .orElse(Collections.emptyList())));
    }

    @Override
    public String getPatternSetting(CoreDataSettingNames key) {
        return this.patternSettings.get(key);
    }

    @Override
    public String getTradingPatternSetting(CoreDataSettingNames key) {
        return this.patternSettings.get(key);
    }

    @Override
    public int getMaxTradingScope(Symbol symbol, Timeframe timeframe) {
        return getTradingPatternBox(symbol, timeframe).map(PatternBox::getMaxScope).orElse(0);
    }

    @Override
    public int getTradingPatternLength(Symbol symbol, Timeframe timeframe) {
        return getTradingPatternBox(symbol, timeframe).map(PatternBox::getPatternsLength).orElse(0);
    }

    @Override
    public boolean canProvideDataForTradingOn(Symbol symbol, Timeframe timeframe) {
        return getTradingPatternBox(symbol, timeframe).isPresent();
    }

    public synchronized boolean purgeUselessData(PurgeOption option) {

        if (option == null) {
            return false;
        }

        switch (option) {
            case GRAPHS -> this.graphs.clear();
            case PATTERNS -> this.patternBoxes.clear();
            case GRAPHS_AND_PATTERNS -> this.purgeNonTrading();
            default -> {
                return false;
            }
        }
        return true;
    }

    private void purgeNonTrading() {
        this.graphs.clear();
        this.patternBoxes.clear();
    }

    @Override
    public String toString() {
        return NEW_LINE + "*** CORE DATA ***" + generateGraphsToPrint() + generatePatternBoxesToPrint() + generateSettingsToPrint();
    }

    private String generateSettingsToPrint() {
        return generateSettingsToPrint(this.patternSettings, "GENERAL SETTINGS") +
                generateSettingsToPrint(this.tradingPatternSettings, "TRADING SETTINGS");
    }

    private String generateSettingsToPrint(Map<CoreDataSettingNames, String> patternSettings, String settingsType) {
        StringBuilder settingsBuilder = new StringBuilder();
        settingsBuilder.append(NEW_LINE).append(settingsType).append(NEW_LINE);

        String mapAsString = patternSettings.keySet().stream()
                .map(key -> key + "=" + patternSettings.get(key))
                .collect(Collectors.joining(", ", "{", "}"));


        settingsBuilder.append(mapAsString).append("***").append(NEW_LINE);

        return settingsBuilder.toString();
    }

    private String generatePatternBoxesToPrint() {
        return generatePatternBoxesToPrint(this.patternBoxes, "PATTERN") +
                generatePatternBoxesToPrint(this.tradingPatternBoxes, "TRADING PATTERN");
    }

    private String generatePatternBoxesToPrint(Set<PatternBox> patternBoxes, String patternType) {

        StringBuilder patternBoxesBuilder = new StringBuilder();

        if (Check.isNotEmpty(patternBoxes)) {
            patternBoxesBuilder.append(NEW_LINE).append(patternBoxes.size()).append(" ").append(patternType).append(" BOX(ES)").append(NEW_LINE);
            for (PatternBox patternBox : patternBoxes) {

                for (Map.Entry<Integer, List<Pattern>> entry : patternBox.getPatterns().entrySet()) {

                    if (entry.getValue() != null) {

                        Pattern anyPattern = patternBox.getPatterns().entrySet().iterator().next().getValue().get(0);

                        patternBoxesBuilder.append("-> ")
                                .append(entry.getValue().size())
                                .append(" patterns, ")
                                .append(patternBox.getSymbol()).append(", ")
                                .append(patternBox.getTimeframe())
                                .append(", pattern scope=")
                                .append(entry.getKey())
                                .append(", pattern type=")
                                .append(anyPattern.getClass())
                                .append(", pattern length=")
                                .append(anyPattern.getLength())
                                .append(", pattern granularity=")
                                .append(anyPattern.getGranularity())
                                .append(NEW_LINE);
                    }

                }
                patternBoxesBuilder.append("***").append(NEW_LINE);
            }
        } else {
            patternBoxesBuilder.append(NEW_LINE).append("0").append(" ").append(patternType).append(" BOX(ES)").append(NEW_LINE).append("***");
        }
        return patternBoxesBuilder.toString();
    }

    private String generateGraphsToPrint() {
        StringBuilder graphsBuilder = new StringBuilder();

        if (Check.isNotEmpty(this.graphs)) {
            graphsBuilder.append(NEW_LINE).append(this.graphs.size()).append(" GRAPH(S)").append(NEW_LINE);
            for (Graph graph : this.graphs) {
                graphsBuilder.append("-> ").append(graph).append(NEW_LINE);
            }
        } else {
            graphsBuilder.append(NEW_LINE).append("0 GRAPH(S)").append(NEW_LINE).append("***");
        }
        return graphsBuilder.toString();
    }

    public Set<Integer> getPatternScopes(Symbol symbol, Timeframe timeframe) {
        return Collections.unmodifiableSet(Objects.requireNonNull(this.getPatternBox(symbol, timeframe))
                .map(PatternBox::getScopes)
                .orElse(Collections.emptySet()));
    }

    public Set<Integer> getTradingPatternScopes(Symbol symbol, Timeframe timeframe) {
        return Collections.unmodifiableSet(Objects.requireNonNull(this.getTradingPatternBox(symbol, timeframe))
                .map(PatternBox::getScopes)
                .orElse(Collections.emptySet()));
    }

}

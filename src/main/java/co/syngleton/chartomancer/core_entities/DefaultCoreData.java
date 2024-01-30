package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.util.Check;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class DefaultCoreData extends CoreData {

    private DefaultCoreData() {
        super();
    }

    private DefaultCoreData(CoreDataSnapshot coreDataSnapshot) {
        super(coreDataSnapshot);
    }

    /**
     * This factory method is used instead of a constructor so the class cannot be extended outside its package,
     * while still being extensible within it.
     *
     * @return a new instance of the class
     */
    public static DefaultCoreData newInstance() {
        return new DefaultCoreData();
    }

    public static DefaultCoreData valueOf(CoreDataSnapshot coreDataSnapshot) {
        return new DefaultCoreData(coreDataSnapshot);
    }

    @Override
    public void setPatternBoxesDeprecated(Set<PatternBox> patternBoxes) {
        this.patternBoxes = patternBoxes;
    }

    @Override
    public Set<PatternBox> getTradingPatternBoxes() {
        return this.tradingPatternBoxes;
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
    public synchronized void addPatterns(List<Pattern> patterns, Symbol symbol, Timeframe timeframe) {
        getPatternBox(symbol, timeframe).ifPresentOrElse(patternBox -> patternBox.addPatterns(patterns),
                () -> patternBoxes.add(new PatternBox(patterns))
        );
    }

    @Override
    public synchronized void putPatterns(List<Pattern> patterns, Symbol symbol, Timeframe timeframe) {
        getPatternBox(symbol, timeframe).ifPresentOrElse(patternBox -> patternBox.putPatterns(patterns),
                () -> patternBoxes.add(new PatternBox(patterns))
        );
    }

    @Override
    public synchronized void copy(@NonNull CoreData coreData) {
        this.graphs = coreData.graphs;
        this.patternBoxes = coreData.patternBoxes;
        this.tradingPatternBoxes = coreData.tradingPatternBoxes;
        this.patternSettings.putAll(coreData.patternSettings);
        this.tradingPatternSettings.putAll(coreData.tradingPatternSettings);
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

        if (this.patternBoxes == null || this.patternBoxes.isEmpty()) {
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

                if (pattern instanceof PredictivePattern predictivePattern) {
                    tradingPatternsList.add(new TradingPattern(predictivePattern));
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
    public void setPatternSetting(String key, String value) {
        this.patternSettings.put(key, value);
    }

    @Override
    public void setTradingPatternSetting(String key, String value) {
        this.tradingPatternSettings.put(key, value);
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
    public String getPatternSetting(String key) {
        return this.patternSettings.get(key);
    }

    @Override
    public String getTradingPatternSetting(String key) {
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
            case GRAPHS -> this.graphs = new HashSet<>();
            case PATTERNS -> this.patternBoxes = new HashSet<>();
            case GRAPHS_AND_PATTERNS -> this.purgeNonTrading();
            default -> {
                return false;
            }
        }
        return true;
    }

    private void purgeNonTrading() {
        graphs = new HashSet<>();
        patternBoxes = new HashSet<>();
    }

    @Override
    public String toString() {
        return NEW_LINE + "*** CORE DATA ***" + generateGraphsToPrint() + generatePatternBoxesToPrint() + generateSettingsToPrint();
    }

    private String generateSettingsToPrint() {
        return generateSettingsToPrint(this.patternSettings, "GENERAL SETTINGS") +
                generateSettingsToPrint(this.tradingPatternSettings, "TRADING SETTINGS");
    }

    private String generateSettingsToPrint(Map<String, String> patternSettings, String settingsType) {
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


}

package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.NonNull;

import java.util.*;

public abstract class CoreData {

    static final String NEW_LINE = System.lineSeparator();
    Set<Graph> graphs;
    Set<PatternBox> patternBoxes;
    Map<String, String> patternSettings;
    Set<PatternBox> tradingPatternBoxes;
    Map<String, String> tradingPatternSettings;

    CoreData() {
        this.graphs = new HashSet<>();
        this.patternBoxes = new HashSet<>();
        this.patternSettings = new HashMap<>();
        this.tradingPatternBoxes = new HashSet<>();
        this.tradingPatternSettings = new HashMap<>();
    }

    CoreData(CoreDataSnapshot coreDataSnapshot) {
        this.graphs = coreDataSnapshot.graphs();
        this.patternBoxes = getPatternBoxesFromSnapshot(coreDataSnapshot.patternBoxes());
        this.patternSettings = coreDataSnapshot.patternSettings();
        this.tradingPatternBoxes = getPatternBoxesFromSnapshot(coreDataSnapshot.tradingPatternBoxes());
        this.tradingPatternSettings = coreDataSnapshot.tradingPatternSettings();
    }

    public CoreDataSnapshot getSnapshot() {
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

    public boolean hasValidStructure() {
        return this.graphs != null && this.patternBoxes != null && this.patternSettings != null
                && this.tradingPatternBoxes != null && this.tradingPatternSettings != null;
    }

    public abstract void addPatterns(List<Pattern> patterns, Symbol symbol, Timeframe timeframe);

    public abstract void putPatterns(List<Pattern> patterns, Symbol symbol, Timeframe timeframe);

    public abstract void copy(@NonNull CoreData coreData);

    public abstract int getTradingPatternLength(Symbol symbol, Timeframe timeframe);

    public abstract boolean canProvideDataForTradingOn(Symbol symbol, Timeframe timeframe);

    public abstract Set<Graph> getReadOnlyGraphs();

    public abstract void addGraph(Graph graph);

    public abstract Set<Graph> getUncomputedGraphs();

    public abstract int getGraphNumber();

    public abstract int getNumberOfPatternSets();

    public abstract int getNumberOfTradingPatternSets();

    public abstract boolean purgeUselessData(PurgeOption option);

    public abstract boolean pushTradingPatternData();

    public abstract Graph getGraph(Symbol symbol, Timeframe timeframe);

    public abstract List<Pattern> getPatterns();

    public abstract List<Pattern> getTradingPatterns();

    public abstract List<Pattern> getPatterns(Symbol symbol, Timeframe timeframe);

    public abstract List<Pattern> getTradingPatterns(Symbol symbol, Timeframe timeframe);

    public abstract List<Pattern> getPatterns(Symbol symbol, Timeframe timeframe, int scope);

    public abstract List<Pattern> getTradingPatterns(Symbol symbol, Timeframe timeframe, int scope);

    public abstract void setPatternSetting(String key, String value);

    public abstract void setTradingPatternSetting(String key, String value);

    public abstract Set<Timeframe> getTradingTimeframes();

    public abstract String getPatternSetting(String key);

    public abstract String getTradingPatternSetting(String key);

    public abstract int getMaxTradingScope(Symbol symbol, Timeframe timeframe);

    //TODO à supprimer (toutes les classes en dessous de cette ligne)
    //public abstract Set<PatternBox> getPatternBoxes();

    public abstract void setPatternBoxesDeprecated(Set<PatternBox> patternBoxes);

    public abstract Set<PatternBox> getTradingPatternBoxes();


}

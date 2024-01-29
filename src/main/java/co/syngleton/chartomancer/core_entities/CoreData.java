package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.NonNull;

import java.util.*;

public abstract class CoreData {

    protected static final String NEW_LINE = System.lineSeparator();
    protected Set<Graph> graphs;
    protected Set<PatternBox> patternBoxes;
    protected Map<String, String> patternSettings;
    protected Set<PatternBox> tradingPatternBoxes;
    protected Map<String, String> tradingPatternSettings;

    protected CoreData() {
        this.graphs = new HashSet<>();
        this.patternBoxes = new HashSet<>();
        this.patternSettings = new HashMap<>();
        this.tradingPatternBoxes = new HashSet<>();
        this.tradingPatternSettings = new HashMap<>();
    }

    protected CoreData(CoreDataSnapshot coreDataSnapshot) {
        this.graphs = coreDataSnapshot.graphs();
        this.patternBoxes = coreDataSnapshot.patternBoxes();
        this.patternSettings = coreDataSnapshot.patternSettings();
        this.tradingPatternBoxes = coreDataSnapshot.tradingPatternBoxes();
        this.tradingPatternSettings = coreDataSnapshot.tradingPatternSettings();
    }

    //TODO Make deep copy of unmodifiable objects when everything else works?
    public CoreDataSnapshot getSnapshot() {
        return new CoreDataSnapshot(graphs, patternBoxes, patternSettings, tradingPatternBoxes, tradingPatternSettings);
    }

    public abstract void addPatterns(List<Pattern> patterns, Symbol symbol, Timeframe timeframe);

    public abstract List<Pattern> getPatterns(Symbol symbol, Timeframe timeframe);

    public abstract void copy(@NonNull CoreData coreData);

    public abstract int getTradingPatternLength(Symbol symbol, Timeframe timeframe);

    public abstract boolean canProvideDataForTradingOn(Symbol symbol, Timeframe timeframe);

    public abstract int getGraphNumber();

    public abstract int getNumberOfPatternSets();

    public abstract int getNumberOfTradingPatternSets();

    public abstract boolean purgeUselessData(PurgeOption option);

    public abstract boolean pushTradingPatternData();

    //Optional<PatternBox> getPatternBox(Symbol symbol, Timeframe timeframe);

    //Optional<PatternBox> getTradingPatternBox(Symbol symbol, Timeframe timeframe);

    //public abstract Set<Graph> getGraphs();

    public abstract Graph getGraph(Symbol symbol, Timeframe timeframe);

    //public abstract void setGraphs(Set<Graph> graphs);

    public abstract List<Pattern> getPatterns(Symbol symbol, Timeframe timeframe, int scope);

    public abstract List<Pattern> getTradingPatterns(Symbol symbol, Timeframe timeframe, int scope);

    //Set<PatternBox> getPatternBoxes();

    //public abstract void setPatternBoxes(Set<PatternBox> patternBoxes);

    //public abstract Map<String, String> getPatternSettings();

    //public abstract void setPatternSettings(Map<String, String> patternSettings);

    public abstract void setPatternSetting(String key, String value);

    //public abstract Set<PatternBox> getTradingPatternBoxes();

    //public abstract void setTradingPatternBoxes(Set<PatternBox> tradingPatternBoxes);

    //public abstract Map<String, String> getTradingPatternSettings();

    //public abstract void setTradingPatternSettings(Map<String, String> tradingPatternSettings);

    public abstract void setTradingPatternSetting(String key, String value);

    public abstract Set<Timeframe> getTradingTimeframes();

    public abstract String getPatternSetting(String key);

    public abstract String getTradingPatternSetting(String key);

    public abstract int getMaxTradingScope(Symbol symbol, Timeframe timeframe);

    //TODO à supprimer
    public abstract Set<PatternBox> getPatternBoxes();

    public abstract void setPatternBoxes(Set<PatternBox> patternBoxes);

    public abstract Set<PatternBox> getTradingPatternBoxes();

    public abstract Set<Graph> getGraphs();
}

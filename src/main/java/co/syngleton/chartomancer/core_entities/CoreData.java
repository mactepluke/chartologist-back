package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

public interface CoreData {
    CoreDataSnapshot getSnapshot();

    boolean hasInvalidStructure();

    void addPatterns(List<Pattern> patterns);

    void putPatterns(List<Pattern> patterns);

    void copy(@NonNull CoreData coreData);

    int getTradingPatternLength(Symbol symbol, Timeframe timeframe);

    boolean canProvideDataForTradingOn(Symbol symbol, Timeframe timeframe);

    Set<Graph> getReadOnlyGraphs();

    void addGraph(Graph graph);

    Set<Graph> getUncomputedGraphs();

    int getGraphNumber();

    int getNumberOfPatternSets();

    int getNumberOfTradingPatternSets();

    boolean purgeUselessData(PurgeOption option);

    boolean pushTradingPatternData();

    Graph getGraph(Symbol symbol, Timeframe timeframe);

    List<Pattern> getPatterns();

    List<Pattern> getTradingPatterns();

    List<Pattern> getPatterns(Symbol symbol, Timeframe timeframe);

    List<Pattern> getTradingPatterns(Symbol symbol, Timeframe timeframe);

    List<Pattern> getPatterns(Symbol symbol, Timeframe timeframe, int scope);

    List<Pattern> getTradingPatterns(Symbol symbol, Timeframe timeframe, int scope);

    Set<Integer> getPatternScopes(Symbol symbol, Timeframe timeframe);

    Set<Integer> getTradingPatternScopes(Symbol symbol, Timeframe timeframe);

    void setPatternSetting(String key, String value);

    Set<Timeframe> getTradingTimeframes();

    String getPatternSetting(String key);

    String getTradingPatternSetting(String key);

    int getMaxTradingScope(Symbol symbol, Timeframe timeframe);

}

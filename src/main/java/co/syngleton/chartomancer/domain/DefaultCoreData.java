package co.syngleton.chartomancer.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class DefaultCoreData implements CoreData {
    private Set<Graph> graphs;
    private Set<PatternBox> patternBoxes;
    private Map<String, String> patternSettings;
    private Set<PatternBox> tradingPatternBoxes;
    private Map<String, String> tradingPatternSettings;


    public DefaultCoreData() {
        this.graphs = new HashSet<>();
        this.patternBoxes = new HashSet<>();
        this.tradingPatternBoxes = new HashSet<>();
        this.patternSettings = new HashMap<>();
        this.tradingPatternSettings = new HashMap<>();
    }

    @Override
    public void copy(@NonNull CoreData coreData) {
        this.graphs = coreData.getGraphs();
        this.patternBoxes = coreData.getPatternBoxes();
        this.tradingPatternBoxes = coreData.getTradingPatternBoxes();
        this.patternSettings.putAll(coreData.getPatternSettings());
        this.tradingPatternSettings.putAll(coreData.getTradingPatternSettings());
    }

    @Override
    public void pushTradingPatternData(Set<PatternBox> patternBoxes) {
        this.tradingPatternBoxes = patternBoxes;
        this.tradingPatternSettings.putAll(patternSettings);
    }

    @Override
    public void purgeNonTrading() {
        graphs = new HashSet<>();
        patternBoxes = new HashSet<>();
    }

    @Override
    public Graph getGraph(Symbol symbol, Timeframe timeframe) {
        for (Graph graph : this.getGraphs()) {
            if (graph.getTimeframe() == timeframe && graph.getSymbol() == symbol) {
                return graph;
            }
        }
        return null;
    }

    @Override
    public Optional<PatternBox> getPatternBox(Symbol symbol, Timeframe timeframe) {
        return this.patternBoxes.stream().filter(patternBox -> patternBox.getTimeframe() == timeframe
                && patternBox.getSymbol() == symbol).findAny();
    }

    @Override
    public Optional<PatternBox> getTradingPatternBox(Symbol symbol, Timeframe timeframe) {
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

}

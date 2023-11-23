package co.syngleton.chartomancer.analytics.data;

import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.PatternBox;
import co.syngleton.chartomancer.analytics.model.Symbol;
import co.syngleton.chartomancer.analytics.model.Timeframe;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class CoreData implements Serializable {
    private Set<Graph> graphs;
    private Set<PatternBox> patternBoxes;
    private DataSettings patternSettings;
    private Set<PatternBox> tradingPatternBoxes;
    private DataSettings tradingPatternSettings;


    public CoreData() {
        this.graphs = new HashSet<>();
        this.patternBoxes = new HashSet<>();
        this.tradingPatternBoxes = new HashSet<>();
        this.patternSettings = new DataSettings();
        this.tradingPatternSettings = new DataSettings();
    }

    public void copy(@NonNull CoreData coreData) {
        this.graphs = coreData.getGraphs();
        this.patternBoxes = coreData.getPatternBoxes();
        this.tradingPatternBoxes = coreData.getTradingPatternBoxes();
        this.patternSettings.copy(coreData.getPatternSettings());
        this.tradingPatternSettings.copy(coreData.getTradingPatternSettings());
    }

    public void pushTradingPatternData(Set<PatternBox> patternBoxes) {
        this.tradingPatternBoxes = patternBoxes;
        this.tradingPatternSettings.copy(patternSettings);
    }

    public void purgeNonTrading() {
        graphs = new HashSet<>();
        patternBoxes = new HashSet<>();
    }

    public Graph getGraph(Symbol symbol, Timeframe timeframe) {
        for (Graph graph : this.getGraphs()) {
            if (graph.getTimeframe() == timeframe && graph.getSymbol() == symbol) {
                return graph;
            }
        }
        return null;
    }

    public Optional<PatternBox> getPatternBox(Symbol symbol, Timeframe timeframe) {
        return this.patternBoxes.stream().filter(patternBox -> patternBox.getTimeframe() == timeframe
                && patternBox.getSymbol() == symbol).findAny();
    }

    public Optional<PatternBox> getTradingPatternBox(Symbol symbol, Timeframe timeframe) {
        return this.tradingPatternBoxes.stream().filter(patternBox -> patternBox.getTimeframe() == timeframe
                && patternBox.getSymbol() == symbol).findAny();
    }

}

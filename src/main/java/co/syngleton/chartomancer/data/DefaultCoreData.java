package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.domain.Graph;
import co.syngleton.chartomancer.domain.PatternBox;
import co.syngleton.chartomancer.domain.Symbol;
import co.syngleton.chartomancer.domain.Timeframe;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
public class DefaultCoreData implements CoreData {
    private Set<Graph> graphs;
    private Set<PatternBox> patternBoxes;
    private DataSettings patternSettings;
    private Set<PatternBox> tradingPatternBoxes;
    private DataSettings tradingPatternSettings;


    public DefaultCoreData() {
        this.graphs = new HashSet<>();
        this.patternBoxes = new HashSet<>();
        this.tradingPatternBoxes = new HashSet<>();
        this.patternSettings = new DataSettings();
        this.tradingPatternSettings = new DataSettings();
    }

    @Override
    public void copy(@NonNull CoreData coreData) {
        this.graphs = coreData.getGraphs();
        this.patternBoxes = coreData.getPatternBoxes();
        this.tradingPatternBoxes = coreData.getTradingPatternBoxes();
        this.patternSettings.copy(coreData.getPatternSettings());
        this.tradingPatternSettings.copy(coreData.getTradingPatternSettings());
    }

    @Override
    public void pushTradingPatternData(Set<PatternBox> patternBoxes) {
        this.tradingPatternBoxes = patternBoxes;
        this.tradingPatternSettings.copy(patternSettings);
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

}

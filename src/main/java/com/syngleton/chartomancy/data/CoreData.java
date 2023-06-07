package com.syngleton.chartomancy.data;

import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.model.charting.patterns.PatternBox;
import lombok.Data;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

@Data
public class CoreData implements Serializable {
    private Set<Graph> graphs;
    private Set<PatternBox> patternBoxes;
    private Set<PatternBox> tradingPatternBoxes;
    private String analyzerConfigSettings;

    public boolean purge() {
        graphs = null;
        patternBoxes = null;
        return true;
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

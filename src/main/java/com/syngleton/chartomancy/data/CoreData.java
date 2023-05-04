package com.syngleton.chartomancy.data;

import com.syngleton.chartomancy.model.charting.misc.ChartObject;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.model.charting.patterns.PatternBox;
import lombok.Data;

import java.util.Set;

@Data
public class CoreData {
    private Set<Graph> graphs;
    private Set<PatternBox> patternBoxes;
    private Set<PatternBox> tradingPatternBoxes;

    public boolean purge()    {
        graphs = null;
        patternBoxes = null;
        return true;
    }

    public Graph getGraph(Symbol symbol, Timeframe timeframe)    {
        for (Graph graph : this.getGraphs())    {
            if (graph.getTimeframe() == timeframe && graph.getSymbol() == symbol) {
                return graph;
            }
        }
        return null;
    }

}

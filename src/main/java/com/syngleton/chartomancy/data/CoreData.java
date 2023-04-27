package com.syngleton.chartomancy.data;

import com.syngleton.chartomancy.model.charting.misc.Graph;
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
}

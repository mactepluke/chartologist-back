package com.syngleton.chartomancy.data;

import com.syngleton.chartomancy.model.charting.Graph;
import com.syngleton.chartomancy.model.charting.PatternBox;
import lombok.Data;

import java.util.Set;

@Data
public class AppData {
    private Set<Graph> graphs;
    private Set<PatternBox> patternBoxes;
}

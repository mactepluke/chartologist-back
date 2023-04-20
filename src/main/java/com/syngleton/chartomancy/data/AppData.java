package com.syngleton.chartomancy.data;

import com.syngleton.chartomancy.model.Graph;
import com.syngleton.chartomancy.model.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class AppData {
    private List<Graph> graphs;
    private List<List<Pattern>> patternsList;
}

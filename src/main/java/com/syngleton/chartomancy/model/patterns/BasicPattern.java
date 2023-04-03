package com.syngleton.chartomancy.model.patterns;

import com.syngleton.chartomancy.model.data.Graph;
import com.syngleton.chartomancy.model.data.Timeframe;

import java.time.LocalDateTime;

public class BasicPattern implements Pattern {

    private Graph graph;
    private short granularity;
    private Timeframe timeframe;
    private String name;
    private LocalDateTime startDate;
}

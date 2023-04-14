package com.syngleton.chartomancy.analytics;

import java.time.LocalDateTime;

public class ComputationData {
    LocalDateTime startTime;
    LocalDateTime endTime;
    ComputationType computationType;
    double computations;
    byte startEfficiency;
    byte endEfficiency;
}

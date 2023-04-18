package com.syngleton.chartomancy.analytics;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class ComputationData {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ComputationType computationType;
    private long computations;
    private byte startPricePrediction;
    private byte endPricePrediction;
}

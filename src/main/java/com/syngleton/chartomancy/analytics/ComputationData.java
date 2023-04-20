package com.syngleton.chartomancy.analytics;

import java.time.LocalDateTime;

public record ComputationData(
    LocalDateTime startTime,
    LocalDateTime endTime,
    ComputationType computationType,
    long computations,
    int startPricePrediction,
    int endPricePrediction
) {

}

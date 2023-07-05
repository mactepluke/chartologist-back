package com.syngleton.chartomancy.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;

public record ComputationSettingsDTO(
        @JsonProperty ComputationSettings.Autoconfig autoconfig,
        @JsonProperty ComputationType computationType,
        @JsonProperty boolean discriminateCandleWicksFromBodies,
        @JsonProperty boolean discriminateCandleColor,
        @JsonProperty boolean adjustMatchingScoreWithVolume,
        @JsonProperty int discardLessPerformingPatternsPercentage,
        @JsonProperty int minPredictivePricePredictionToKeep
) {

    public ComputationSettingsDTO() {
        this(
                null,
                null,
                false,
                false,
                false,
                0,
                0
        );
    }
}

package co.syngleton.chartomancer.dto.internal;

import co.syngleton.chartomancer.analytics.ComputationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import co.syngleton.chartomancer.analytics.ComputationSettings;

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

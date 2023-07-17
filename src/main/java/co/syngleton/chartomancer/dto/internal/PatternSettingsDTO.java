package co.syngleton.chartomancer.dto.internal;

import co.syngleton.chartomancer.model.charting.misc.PatternType;
import com.fasterxml.jackson.annotation.JsonProperty;
import co.syngleton.chartomancer.factory.PatternSettings;

public record PatternSettingsDTO(
        @JsonProperty PatternType patternType,
        @JsonProperty PatternSettings.Autoconfig autoconfig,
        @JsonProperty int granularity,
        @JsonProperty int length,
        @JsonProperty int scope
) {
    public PatternSettingsDTO(
            @JsonProperty PatternType patternType,
            @JsonProperty PatternSettings.Autoconfig autoconfig
    ) {
        this(patternType, autoconfig, 0, 0, 0);
    }

    public PatternSettingsDTO(
            @JsonProperty PatternType patternType,
            @JsonProperty PatternSettings.Autoconfig autoconfig,
            @JsonProperty int granularity,
            @JsonProperty int length
    ) {
        this(patternType, autoconfig, 0, 0, 0);
    }
}

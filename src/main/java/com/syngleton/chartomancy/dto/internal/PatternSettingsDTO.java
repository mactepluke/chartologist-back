package com.syngleton.chartomancy.dto.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.charting.misc.PatternType;

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

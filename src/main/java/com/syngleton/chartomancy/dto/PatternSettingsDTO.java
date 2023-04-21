package com.syngleton.chartomancy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.syngleton.chartomancy.model.charting.PatternType;
import com.syngleton.chartomancy.factory.PatternSettings;

public record PatternSettingsDTO(
        @JsonProperty PatternType patternType,
        @JsonProperty PatternSettings.Autoconfig autoconfig,
        @JsonProperty int granularity,
        @JsonProperty int length,
        @JsonProperty String name,
        @JsonProperty int scope
) {
    public PatternSettingsDTO(
            @JsonProperty PatternType patternType,
            @JsonProperty PatternSettings.Autoconfig autoconfig,
            @JsonProperty String name
    ) {
        this(patternType, autoconfig, 0, 0, name, 0);
    }

    public PatternSettingsDTO(
            @JsonProperty PatternType patternType,
            @JsonProperty PatternSettings.Autoconfig autoconfig,
            @JsonProperty int granularity,
            @JsonProperty int length,
            @JsonProperty String name
    ) {
        this(patternType, autoconfig, 0, 0, name, 0);
    }
}

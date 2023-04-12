package com.syngleton.chartomancy.model.patterns;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.syngleton.chartomancy.model.patterns.PatternTypes;
import com.syngleton.chartomancy.service.patterns.PatternSettings;

public record PatternSettingsDTO(
        @JsonProperty PatternTypes patternType,
        @JsonProperty PatternSettings.Autoconfig autoconfig,
        @JsonProperty int granularity,
        @JsonProperty int length,
        @JsonProperty String name,
        @JsonProperty int span
) {
    public PatternSettingsDTO(
            @JsonProperty PatternTypes patternType,
            @JsonProperty PatternSettings.Autoconfig autoconfig,
            @JsonProperty String name
    ) {
        this(patternType, autoconfig, 0, 0, name, 0);
    }

    public PatternSettingsDTO(
            @JsonProperty PatternTypes patternType,
            @JsonProperty PatternSettings.Autoconfig autoconfig,
            @JsonProperty int granularity,
            @JsonProperty int length,
            @JsonProperty String name
    ) {
        this(patternType, autoconfig, 0, 0, name, 0);
    }
}

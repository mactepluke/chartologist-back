package com.syngleton.chartomancy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.syngleton.chartomancy.model.patterns.PatternTypes;
import com.syngleton.chartomancy.service.patterns.PatternSettings;

public record PatternSettingsDTO(
        @JsonProperty PatternTypes patternType,
        @JsonProperty PatternSettings.Autoconfig autoconfig,
        @JsonProperty int granularity,
        @JsonProperty int length
) {
}

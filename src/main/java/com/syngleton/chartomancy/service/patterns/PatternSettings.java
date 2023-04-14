package com.syngleton.chartomancy.service.patterns;

import com.syngleton.chartomancy.model.patterns.PatternSettingsDTO;
import com.syngleton.chartomancy.model.dataloading.Graph;
import com.syngleton.chartomancy.model.patterns.PatternTypes;
import lombok.*;

@ToString
public class PatternSettings {

    @ToString.Exclude
    @Getter
    private final Graph graph;
    @Getter
    private final PatternTypes patternType;
    @Getter
    private final Autoconfig autoconfig;
    @Getter
    private final String name;
    @Getter
    private final int granularity;
    @Getter
    private final int length;
    @Getter
    private final int span;

    public enum Autoconfig {
        NONE,
        MINIMIZE,
        MAXIMIZE,
        USE_DEFAULTS,
        BYPASS_SAFETY_CHECK,
        TEST
    }

    private PatternSettings(Builder builder) {
        this.graph = builder.graph;
        this.patternType = builder.patternType;
        this.name = builder.name;
        this.granularity = builder.granularity;
        this.length = builder.length;
        this.autoconfig = builder.autoconfig;
        this.span = builder.span;
    }

    public static class Builder {
        private Graph graph = null;
        private PatternTypes patternType = PatternTypes.BASIC;
        private Autoconfig autoconfig = Autoconfig.NONE;
        private String name = "No Name";
        private int granularity = 1;
        private int span = 1;
        private int length;

        public Builder patternType(PatternTypes patternType) {
            if (patternType != null) {
                this.patternType = patternType;
            }
            return this;
        }

        public Builder map(PatternSettingsDTO patternSettingsDTO)   {

            if (patternSettingsDTO != null) {
                this.patternType = patternSettingsDTO.patternType();
                this.autoconfig = patternSettingsDTO.autoconfig();
                this.granularity = patternSettingsDTO.granularity();
                this.length = patternSettingsDTO.length();
                this.name = patternSettingsDTO.name();
                this.span = patternSettingsDTO.span();
            }
            return this;
        }

        public Builder graph(Graph graph) {
            if (graph != null) {
                this.graph = graph;
            }
            return this;
        }

        public Builder name(String name) {
            if (!name.isEmpty()) {
                this.name = name;
            }
            return this;
        }

        public Builder granularity(int granularity) {
            this.granularity = granularity;
            return this;
        }

        public Builder span(int span) {
            this.span = span;
            return this;
        }

        public Builder length(int length) {
            this.length = length;
            return this;
        }

        public Builder autoconfig(Autoconfig autoconfig) {
            if (autoconfig != null) {
                this.autoconfig = autoconfig;
            }
            return this;
        }
        public PatternSettings build() {
            return new PatternSettings(this);
        }
    }
}

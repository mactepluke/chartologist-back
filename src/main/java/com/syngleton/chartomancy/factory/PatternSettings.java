package com.syngleton.chartomancy.factory;

import com.syngleton.chartomancy.dto.PatternSettingsDTO;
import com.syngleton.chartomancy.model.charting.Graph;
import com.syngleton.chartomancy.model.charting.PatternType;
import lombok.*;

@ToString
public class PatternSettings {

    @Getter
    private final Autoconfig autoconfig;
    @ToString.Exclude
    @Getter
    private final Graph graph;
    @Getter
    private final PatternType patternType;
    @Getter
    private final String name;
    @Getter
    private final int granularity;
    @Getter
    private final int length;
    @Getter
    private final int scope;

    public enum Autoconfig {
        NONE,
        MINIMIZE,
        MAXIMIZE,
        USE_DEFAULTS,
        AUTOMATE,
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
        this.scope = builder.scope;
    }

    public static class Builder {
        private Graph graph = null;
        private PatternType patternType = PatternType.BASIC;
        private Autoconfig autoconfig = Autoconfig.USE_DEFAULTS;
        private String name = "No Name";
        private int granularity = 1;
        private int scope = 1;
        private int length;

        public Builder patternType(PatternType patternType) {
            if (patternType != null) {
                this.patternType = patternType;
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

        public Builder scope(int scope) {
            this.scope = scope;
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

        public Builder map(PatternSettingsDTO patternSettingsDTO)   {

            if (patternSettingsDTO != null) {

                this.patternType = patternSettingsDTO.patternType() != null ? patternSettingsDTO.patternType() : this.patternType;
                this.autoconfig = patternSettingsDTO.autoconfig() != null ? patternSettingsDTO.autoconfig() : this.autoconfig;
                this.granularity = patternSettingsDTO.granularity();
                this.length = patternSettingsDTO.length();
                this.name = patternSettingsDTO.name();
                this.scope = patternSettingsDTO.scope();
            }
            return this;
        }

        public PatternSettings build() {
            return new PatternSettings(this);
        }
    }
}

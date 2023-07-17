package co.syngleton.chartomancer.factory;

import co.syngleton.chartomancer.dto.internal.PatternSettingsDTO;
import co.syngleton.chartomancer.model.charting.misc.Graph;
import co.syngleton.chartomancer.model.charting.misc.PatternType;
import lombok.Getter;
import lombok.ToString;

@ToString
public final class PatternSettings {

    @Getter
    private final Autoconfig autoconfig;
    @ToString.Exclude
    @Getter
    private final Graph graph;
    @Getter
    private final PatternType patternType;
    @Getter
    private final int granularity;
    @Getter
    private final int length;
    @Getter
    private final int scope;
    @Getter
    private final boolean fullScope;
    @Getter
    private final boolean atomicPartition;

    private PatternSettings(Builder builder) {
        this.graph = builder.graph;
        this.patternType = builder.patternType;
        this.granularity = builder.granularity;
        this.length = builder.length;
        this.autoconfig = builder.autoconfig;
        this.scope = builder.scope;
        this.fullScope = builder.fullScope;
        this.atomicPartition = builder.atomicPartition;
    }

    public enum Autoconfig {
        NONE,
        MINIMIZE,
        MAXIMIZE,
        DEFAULT,
        TIMEFRAME,
        TIMEFRAME_LONG,
        TIMEFRAME_VERY_LONG,
        HALF_LENGTH,
        EQUAL_LENGTH,
        THIRD_LENGTH,
        TWO_THIRDS_LENGTH,
        BYPASS_SAFETY_CHECK,
        TEST
    }

    public static class Builder {
        private Graph graph = null;
        private PatternType patternType = PatternType.BASIC;
        private Autoconfig autoconfig = Autoconfig.DEFAULT;
        private int granularity = 1;
        private int scope = 1;
        private int length;
        private boolean fullScope = false;
        private boolean atomicPartition = false;

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

        public Builder granularity(int granularity) {
            this.granularity = granularity;
            return this;
        }

        public Builder scope(int scope) {
            this.scope = scope;
            return this;
        }

        public Builder atomizePartition() {
            this.atomicPartition = true;
            return this;
        }

        public Builder scope(String scope) {
            if (scope.equals("FULL")) {
                this.fullScope = true;
            }
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

        public Builder map(PatternSettingsDTO patternSettingsDTO) {

            if (patternSettingsDTO != null) {
                this.patternType = patternSettingsDTO.patternType() != null ? patternSettingsDTO.patternType() : this.patternType;
                this.autoconfig = patternSettingsDTO.autoconfig() != null ? patternSettingsDTO.autoconfig() : this.autoconfig;
                this.granularity = patternSettingsDTO.granularity();
                this.length = patternSettingsDTO.length();
                this.scope = patternSettingsDTO.scope();
            }
            return this;
        }

        public PatternSettings build() {
            return new PatternSettings(this);
        }
    }
}

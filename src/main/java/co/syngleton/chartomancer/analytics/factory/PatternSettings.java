package co.syngleton.chartomancer.analytics.factory;

import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.PatternType;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public final class PatternSettings {

    private final Autoconfig autoconfig;
    @ToString.Exclude
    private final Graph graph;
    private final PatternType patternType;
    private final int granularity;
    private final int length;
    private final int scope;
    private final boolean fullScope;
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

        public PatternSettings build() {
            return new PatternSettings(this);
        }
    }
}

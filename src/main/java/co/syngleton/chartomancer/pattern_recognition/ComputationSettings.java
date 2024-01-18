package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.shared_domain.Graph;
import co.syngleton.chartomancer.shared_domain.Pattern;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public final class ComputationSettings {

    private final Autoconfig autoconfig;
    private final ComputationType computationType;
    @ToString.Exclude
    private final Graph graph;
    private final List<Pattern> patterns;

    private ComputationSettings(Builder builder) {
        this.autoconfig = builder.autoconfig;
        this.computationType = builder.computationType;
        this.graph = builder.graph;
        this.patterns = builder.patterns;
    }

    public enum Autoconfig {
        NONE,
        DEFAULT,
        TEST
    }

    public static class Builder {
        private Autoconfig autoconfig = Autoconfig.DEFAULT;
        private ComputationType computationType = ComputationType.BASIC_ITERATION;
        private Graph graph = null;
        private List<Pattern> patterns = null;


        public Builder autoconfig(ComputationSettings.Autoconfig autoconfig) {
            if (autoconfig != null) {
                this.autoconfig = autoconfig;
            }
            return this;
        }

        public Builder computationType(ComputationType computationType) {
            if (computationType != null) {
                this.computationType = computationType;
            }
            return this;
        }

        public Builder graph(Graph graph) {
            if (graph != null) {
                this.graph = graph;
            }
            return this;
        }

        public Builder patterns(List<Pattern> patterns) {
            if (!patterns.isEmpty()) {
                this.patterns = patterns;
            }
            return this;
        }

        public ComputationSettings build() {
            return new ComputationSettings(this);
        }
    }
}

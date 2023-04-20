package com.syngleton.chartomancy.analytics;

import com.syngleton.chartomancy.dto.ComputationSettingsDTO;
import com.syngleton.chartomancy.model.Graph;
import com.syngleton.chartomancy.model.Pattern;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
public class ComputationSettings {

    @Getter
    private final Autoconfig autoconfig;
    @Getter
    private final ComputationType computationType;
    @ToString.Exclude
    @Getter
    private final Graph graph;
    @Getter
    private final List<Pattern> patterns;
    @Getter
    private final boolean discriminateCandleWicksFromBodies;
    @Getter
    private final boolean discriminateCandleColor;
    @Getter
    private final boolean adjustMatchingScoreWithVolume;
    @Getter
    private final int discardLessPerformingPatternsPercentage;
    @Getter
    private final int minPredictivePricePredictionToKeep;

    public enum Autoconfig {
        NONE,
        USE_DEFAULTS,
        TEST
    }

    private ComputationSettings(Builder builder)    {
        this.autoconfig = builder.autoconfig;
        this.computationType = builder.computationType;
        this.graph = builder.graph;
        this.patterns = builder.patterns;
        this.discriminateCandleWicksFromBodies = builder.discriminateCandleWicksFromBodies;
        this.discriminateCandleColor = builder.discriminateCandleColor;
        this.adjustMatchingScoreWithVolume = builder.adjustMatchingScoreWithVolume;
        this.discardLessPerformingPatternsPercentage = builder.discardLessPerformingPatternsPercentage;
        this.minPredictivePricePredictionToKeep = builder.minPredictivePricePredictionToKeep;
    }


    public static class Builder    {
        private Autoconfig autoconfig = Autoconfig.USE_DEFAULTS;
        private ComputationType computationType = ComputationType.BASIC_ITERATION;
        private Graph graph = null;
        private List<Pattern> patterns = null;
        private boolean discriminateCandleWicksFromBodies;
        private boolean discriminateCandleColor;
        private boolean adjustMatchingScoreWithVolume;
        private int discardLessPerformingPatternsPercentage;
        private int minPredictivePricePredictionToKeep;


        public Builder autoconfig(ComputationSettings.Autoconfig autoconfig) {
            if (autoconfig != null) {
                this.autoconfig = autoconfig;
            }
            return this;
        }

        public Builder computationType(ComputationType computationType)    {
            if (computationType != null)   {
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

        public Builder discriminateCandleWicksFromBodies(boolean discriminateCandleWicksFromBodies) {
            this.discriminateCandleWicksFromBodies = discriminateCandleWicksFromBodies;
            return this;
        }

        public Builder discriminateCandleColor(boolean discriminateCandleColor) {
            this.discriminateCandleColor = discriminateCandleColor;
            return this;
        }

        public Builder adjustMatchingScoreWithVolume(boolean adjustMatchingScoreWithVolume) {
            this.adjustMatchingScoreWithVolume = adjustMatchingScoreWithVolume;
            return this;
        }

        public Builder discardLessPerformingPatternsPercentage(int discardLessPerformingPatternsPercentage) {
            this.discardLessPerformingPatternsPercentage = discardLessPerformingPatternsPercentage;
            return this;
        }

        public Builder minPredictivePricePredictionToKeep(int minPredictivePricePredictionToKeep) {
            this.minPredictivePricePredictionToKeep = minPredictivePricePredictionToKeep;
            return this;
        }

        public Builder map(ComputationSettingsDTO computationSettingsDTO)   {

            if (computationSettingsDTO != null) {
                this.autoconfig = computationSettingsDTO.autoconfig() != null ? computationSettingsDTO.autoconfig() : this.autoconfig;
                this.computationType = computationSettingsDTO.computationType() != null ? computationSettingsDTO.computationType() : this.computationType;
                this.discriminateCandleWicksFromBodies = computationSettingsDTO.discriminateCandleWicksFromBodies();
                this.discriminateCandleColor = computationSettingsDTO.discriminateCandleColor();
                this.adjustMatchingScoreWithVolume = computationSettingsDTO.adjustMatchingScoreWithVolume();
                this.discardLessPerformingPatternsPercentage = computationSettingsDTO.discardLessPerformingPatternsPercentage();
                this.minPredictivePricePredictionToKeep = computationSettingsDTO.minPredictivePricePredictionToKeep();
            }
            return this;
        }

        public ComputationSettings build() {
            return new ComputationSettings(this);
        }
    }
}

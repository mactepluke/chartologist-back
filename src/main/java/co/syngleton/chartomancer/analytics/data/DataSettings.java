package co.syngleton.chartomancer.analytics.data;

import co.syngleton.chartomancer.analytics.computation.ComputationSettings;
import co.syngleton.chartomancer.analytics.computation.ComputationType;
import co.syngleton.chartomancer.analytics.computation.Smoothing;
import co.syngleton.chartomancer.analytics.factory.PatternSettings;
import co.syngleton.chartomancer.analytics.model.PatternType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public final class DataSettings implements Serializable {
    private Smoothing matchScoreSmoothing;
    private int matchScoreThreshold;
    private int priceVariationThreshold;
    private boolean extrapolatePriceVariation;
    private boolean extrapolateMatchScore;
    private PatternSettings.Autoconfig patternAutoconfig;
    private ComputationSettings.Autoconfig computationAutoconfig;
    private ComputationType computationType;
    private PatternType computationPatternType;
    private boolean atomicPartition;
    private int scope;
    private boolean fullScope;
    private int patternLength;
    private int patternGranularity;
    private LocalDateTime computationDate;

    public DataSettings() {
    }

    public void copy(DataSettings dataSettings) {
        this.matchScoreSmoothing = dataSettings.matchScoreSmoothing;
        this.matchScoreThreshold = dataSettings.matchScoreThreshold;
        this.priceVariationThreshold = dataSettings.priceVariationThreshold;
        this.extrapolatePriceVariation = dataSettings.extrapolatePriceVariation;
        this.extrapolateMatchScore = dataSettings.extrapolateMatchScore;
        this.patternAutoconfig = dataSettings.patternAutoconfig;
        this.computationAutoconfig = dataSettings.computationAutoconfig;
        this.computationType = dataSettings.computationType;
        this.computationPatternType = dataSettings.computationPatternType;
        this.atomicPartition = dataSettings.atomicPartition;
        this.scope = dataSettings.scope;
        this.fullScope = dataSettings.fullScope;
        this.patternLength = dataSettings.patternLength;
        this.patternGranularity = dataSettings.patternGranularity;
        this.computationDate = dataSettings.computationDate;
    }
}

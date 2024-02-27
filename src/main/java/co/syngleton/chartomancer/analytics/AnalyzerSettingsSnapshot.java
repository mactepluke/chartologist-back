package co.syngleton.chartomancer.analytics;

public record AnalyzerSettingsSnapshot(Smoothing matchScoreSmoothing,
                                       int matchScoreThreshold,
                                       int priceVariationThreshold,
                                       boolean extrapolatePriceVariation,
                                       boolean extrapolateMatchScore) {
}

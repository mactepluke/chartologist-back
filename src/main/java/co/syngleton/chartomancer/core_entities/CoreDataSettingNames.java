package co.syngleton.chartomancer.core_entities;

public enum CoreDataSettingNames {
    MATCH_SCORE_SMOOTHING("match_score_smoothing"),
    MATCH_SCORE_THRESHOLD("match_score_threshold"),
    PRICE_VARIATION_THRESHOLD("price_variation_threshold"),
    EXTRAPOLATE_PRICE_VARIATION("extrapolate_price_variation"),
    EXTRAPOLATE_MATCH_SCORE("extrapolate_match_score"),
    PATTERN_AUTOCONFIG("pattern_autoconfig"),
    COMPUTATION_AUTOCONFIG("computation_autoconfig"),
    COMPUTATION_TYPE("computation_type"),
    COMPUTATION_PATTERN_TYPE("computation_pattern_type"),
    ATOMIC_PARTITION("atomic_partition"),
    SCOPE("scope"),
    FULL_SCOPE("full_scope"),
    PATTERN_LENGTH("pattern_length"),
    PATTERN_GRANULARITY("pattern_granularity"),
    COMPUTATION_DATE("computation_date");

    private final String name;

    CoreDataSettingNames(String name) {
        this.name = name;
    }
}

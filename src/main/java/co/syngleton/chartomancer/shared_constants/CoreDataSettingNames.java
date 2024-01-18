package co.syngleton.chartomancer.shared_constants;

public class CoreDataSettingNames {
    public static final String MATCH_SCORE_SMOOTHING = "match_score_smoothing";
    public static final String MATCH_SCORE_THRESHOLD = "match_score_threshold";
    public static final String PRICE_VARIATION_THRESHOLD = "price_variation_threshold";
    public static final String EXTRAPOLATE_PRICE_VARIATION = "extrapolate_price_variation";
    public static final String EXTRAPOLATE_MATCH_SCORE = "extrapolate_match_score";
    public static final String PATTERN_AUTOCONFIG = "pattern_autoconfig";
    public static final String COMPUTATION_AUTOCONFIG = "computation_autoconfig";
    public static final String COMPUTATION_TYPE = "computation_type";
    public static final String COMPUTATION_PATTERN_TYPE = "computation_pattern_type";
    public static final String ATOMIC_PARTITION = "atomic_partition";
    public static final String SCOPE = "scope";
    public static final String FULL_SCOPE = "full_scope";
    public static final String PATTERN_LENGTH = "pattern_length";
    public static final String PATTERN_GRANULARITY = "pattern_granularity";
    public static final String COMPUTATION_DATE = "computation_date";

    private CoreDataSettingNames() throws IllegalAccessException {
        throw new IllegalAccessException();
    }
}

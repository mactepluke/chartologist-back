package co.syngleton.chartomancer.model.trading;

public enum ProfitFactor {
    UNKNOWN(-1),
    BAD(0),
    RISKY(1),
    GOOD(1.20),
    IDEAL(2),
    OUTSTANDING(3);

    public final double threshold;

    ProfitFactor(double threshold) {
        this.threshold = threshold;
    }

    public static ProfitFactor getQualification(double factor) {

        if (factor > BAD.threshold && factor < RISKY.threshold) {
            return BAD;
        }
        if (factor >= RISKY.threshold && factor < GOOD.threshold) {
            return RISKY;
        }
        if (factor >= GOOD.threshold && factor < IDEAL.threshold) {
            return GOOD;
        }
        if (factor >= IDEAL.threshold && factor < OUTSTANDING.threshold) {
            return IDEAL;
        }
        if (factor >= OUTSTANDING.threshold) {
            return OUTSTANDING;
        }
        return UNKNOWN;
    }

}

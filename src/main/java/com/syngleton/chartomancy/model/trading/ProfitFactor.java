package com.syngleton.chartomancy.model.trading;

public enum ProfitFactor {
    UNKNOWN(-1),
    UNPROFITABLE(0),
    RISKY(1),
    GOOD(1.20),
    IDEAL(2),
    OUTSTANDING(3);

    public final double threshold;

    ProfitFactor(double threshold) {
        this.threshold = threshold;
    }

    public static ProfitFactor getQualification(double factor) {

        if (factor > UNPROFITABLE.threshold && factor < RISKY.threshold)  {
            return UNPROFITABLE;
        }
        if (factor >= RISKY.threshold && factor < GOOD.threshold)  {
            return RISKY;
        }
        if (factor >= GOOD.threshold && factor < IDEAL.threshold)  {
            return GOOD;
        }
        if (factor >= IDEAL.threshold && factor < IDEAL.threshold)  {
            return IDEAL;
        }
        if (factor >= OUTSTANDING.threshold)  {
            return OUTSTANDING;
        }
        return UNKNOWN;
    }

}

package co.syngleton.chartomancer.core_entities;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

import static java.lang.Math.abs;

@Log4j2
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class MultiComputablePattern extends ComputablePattern {

    private final float[] priceVariationPredictions;

    public MultiComputablePattern(Pattern pattern, int scope) {
        super(pattern, scope);
        if (scope < 1) {
            throw new IllegalArgumentException("Scope must be greater than 0.");
        }
        this.priceVariationPredictions = new float[scope];
    }

    /**
     * This method returns  by returning the most relevant price prediction.
     * It is overriden so this class is retro-compatible with the way the other components work
     * (as per the Liskov-substitution principle) when they need PredictivePattern to return
     * a single price prediction.
     *
     * @return the biggest price prediction, whether it is positive or negative.
     */
    @Override
    public float getPriceVariationPrediction() {

        float[] ar = Arrays.copyOf(this.priceVariationPredictions, this.priceVariationPredictions.length);
        Arrays.sort(ar);
        float min = ar[0];
        float max = ar[ar.length - 1];
        return abs(min) > abs(max) ? min : max;
    }

    @Override
    public void setPriceVariationPrediction(float priceVariationPrediction) {
        this.priceVariationPredictions[this.getScope() - 1] = priceVariationPrediction;
    }

    public float getPriceVariationPrediction(int index) {
        checkIndex(index);
        return this.priceVariationPredictions[index - 1];
    }

    public void setPriceVariationPrediction(float priceVariationPrediction, int index) {
        checkIndex(index);
        this.priceVariationPredictions[index - 1] = priceVariationPrediction;
    }

    private void checkIndex(int index) {
        if (index < 1 || index > this.getScope()) {
            throw new IllegalArgumentException("Price variation prediction out of range. Index must be between 1 and " + this.getScope() + ".");
        }
    }

}

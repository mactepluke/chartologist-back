package co.syngleton.chartomancer.core_entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ComputablePattern extends PredictivePattern {

    public ComputablePattern(Pattern pattern, int scope) {
        super(
                pattern.getGranularity(),
                pattern.getSymbol(),
                pattern.getTimeframe(),
                pattern.getIntCandles(),
                scope
        );
    }

    public void setPriceVariationPrediction(float priceVariationPrediction) {
        this.priceVariationPrediction = priceVariationPrediction;
    }

}
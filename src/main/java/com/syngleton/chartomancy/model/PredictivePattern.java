package com.syngleton.chartomancy.model;

import com.syngleton.chartomancy.analytics.ComputationData;
import com.syngleton.chartomancy.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static com.syngleton.chartomancy.util.Format.relativePercentage;

@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class PredictivePattern extends Pattern {

    private final int scope;
    private final List<ComputationData> computationsHistory;
    private int priceVariationPrediction = 0;

    public PredictivePattern(Pattern pattern, int scope) {
        super(
                pattern.getPixelatedCandles(),
                PatternType.PREDICTIVE,
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe(),
                pattern.getName(),
                pattern.getStartDate()
                );
        this.scope = Format.streamlineInt(scope, 1, this.getLength());
        this.computationsHistory = new ArrayList<>();
    }

    public void setPriceVariationPrediction(int priceVariationPrediction) {
        this.priceVariationPrediction = relativePercentage(priceVariationPrediction, 100);
    }
}

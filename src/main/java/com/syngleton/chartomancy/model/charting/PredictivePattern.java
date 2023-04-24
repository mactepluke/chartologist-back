package com.syngleton.chartomancy.model.charting;

import com.syngleton.chartomancy.analytics.ComputationData;
import com.syngleton.chartomancy.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
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
    private final LocalDateTime startDate;

    public PredictivePattern(BasicPattern pattern, int scope) {
        super(
                pattern.getPixelatedCandles(),
                PatternType.PREDICTIVE,
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe()
                );
        this.startDate = pattern.getStartDate();
        this.scope = Format.streamlineInt(scope, 1, this.getLength());
        this.computationsHistory = new ArrayList<>();
    }

    public void setPriceVariationPrediction(int priceVariationPrediction) {
        this.priceVariationPrediction = relativePercentage(priceVariationPrediction, 100);
    }
}

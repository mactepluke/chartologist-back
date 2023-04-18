package com.syngleton.chartomancy.model;

import com.syngleton.chartomancy.analytics.ComputationData;
import com.syngleton.chartomancy.util.Format;
import com.syngleton.chartomancy.util.MiscUtils;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.syngleton.chartomancy.util.Format.*;

@SuppressWarnings("CopyConstructorMissesField")
@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class PredictivePattern extends Pattern {

    private int scope;
    private byte priceVariationPrediction = 0;
    private List<ComputationData> computationsHistory;

    public PredictivePattern() {
    }

    public PredictivePattern(BasicPattern basicPattern) {
        MiscUtils.getMapper().map(basicPattern, this);
    }

    public PredictivePattern(PredictivePattern predictivePattern) {
        MiscUtils.getMapper().map(predictivePattern, this);
    }

    public void setScope(int scope) {
        this.scope = Format.streamlineInt(scope, 1, this.getLength());
    }

    public void setPriceVariationPrediction(int priceVariationPrediction) {
        this.priceVariationPrediction = byteRelativePercentage(priceVariationPrediction, 100);
    }

    public void addComputationsHistory(ComputationData data) {
        if (this.computationsHistory == null)   {
            this.computationsHistory = new ArrayList<>();
        }
        this.computationsHistory.add(data);
    }
}

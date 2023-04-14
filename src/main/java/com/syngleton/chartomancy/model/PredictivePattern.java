package com.syngleton.chartomancy.model;

import com.syngleton.chartomancy.analytics.ComputationData;
import lombok.*;

import java.util.List;

@ToString
@Data
@EqualsAndHashCode(callSuper = true)
public class PredictivePattern extends BasicPattern {
    private int scope;
    private byte pricePrediction;
    private byte efficiency;
    private List<ComputationData> computationsHistory;
}

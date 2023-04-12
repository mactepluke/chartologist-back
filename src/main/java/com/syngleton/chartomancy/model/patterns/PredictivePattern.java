package com.syngleton.chartomancy.model.patterns;

import lombok.*;

@ToString
@Data
@EqualsAndHashCode(callSuper = true)
public class PredictivePattern extends BasicPattern {
    private int span;
    private int scope;
    private int deltaPercent;
    private boolean up;
}

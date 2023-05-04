package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.analytics.ComputationData;
import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import com.syngleton.chartomancy.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class LightPredictivePattern extends Pattern implements ComputablePattern, IntPattern {

    @ToString.Exclude
    private final List<IntCandle> intCandles;
    private final int scope;
    private final List<ComputationData> computationsHistory;
    @Setter
    private float priceVariationPrediction = 0;
    private final LocalDateTime startDate;

    public LightPredictivePattern(LightBasicPattern pattern, int scope) {
        super(
                PatternType.LIGHT_PREDICTIVE,
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe()
        );
        this.intCandles = pattern.getIntCandles();
        this.startDate = pattern.getStartDate();
        this.scope = Format.streamline(scope, 1, this.getLength());
        this.computationsHistory = new ArrayList<>();
    }

}
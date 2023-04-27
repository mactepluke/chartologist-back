package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.analytics.ComputationData;
import com.syngleton.chartomancy.model.charting.candles.PixelatedCandle;
import com.syngleton.chartomancy.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.syngleton.chartomancy.util.Format.relativePercentage;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PredictivePattern extends Pattern implements PixelatedPattern {

    @ToString.Exclude
    private final List<PixelatedCandle> pixelatedCandles;
    @Getter
    private final int scope;
    @Getter
    private final List<ComputationData> computationsHistory;
    @Getter
    private int priceVariationPrediction = 0;
    @Getter
    private final LocalDateTime startDate;

    public PredictivePattern(BasicPattern pattern, int scope) {
        super(
                PatternType.PREDICTIVE,
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe()
                );
        this.pixelatedCandles = pattern.getPixelatedCandles();
        this.startDate = pattern.getStartDate();
        this.scope = Format.streamlineInt(scope, 1, this.getLength());
        this.computationsHistory = new ArrayList<>();
    }

    public void setPriceVariationPrediction(int priceVariationPrediction) {
        this.priceVariationPrediction = relativePercentage(priceVariationPrediction, 100);
    }

    @Override
    public List<PixelatedCandle> getPixelatedCandles() {
        return pixelatedCandles;
    }
}

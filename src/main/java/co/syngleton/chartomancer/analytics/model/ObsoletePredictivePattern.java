package co.syngleton.chartomancer.analytics.model;

import co.syngleton.chartomancer.global.tools.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ObsoletePredictivePattern extends Pattern implements PixelatedPattern, ComputablePattern {

    @ToString.Exclude
    private final List<PixelatedCandle> pixelatedCandles;
    @Getter
    private final int scope;
    @Getter
    private final LocalDateTime startDate;
    @Getter
    @Setter
    private float priceVariationPrediction = 0;

    public ObsoletePredictivePattern(ObsoleteBasicPattern pattern, int scope) {
        super(
                PatternType.PREDICTIVE_OBSOLETE,
                pattern.getGranularity(),
                pattern.getLength(),
                pattern.getSymbol(),
                pattern.getTimeframe()
        );
        this.pixelatedCandles = pattern.getPixelatedCandles();
        this.startDate = pattern.getStartDate();
        this.scope = Format.streamline(scope, 1, this.getLength());
    }

    @Override
    public List<PixelatedCandle> getPixelatedCandles() {
        return pixelatedCandles;
    }
}

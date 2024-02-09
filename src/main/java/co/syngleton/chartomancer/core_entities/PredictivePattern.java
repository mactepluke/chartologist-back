package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.util.Format;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class PredictivePattern extends Pattern {

    private final int scope;
    protected float priceVariationPrediction = 0;

    PredictivePattern(
            int granularity,
            Symbol symbol,
            Timeframe timeframe,
            List<IntCandle> candles,
            int scope
    ) {
        super(
                granularity,
                symbol,
                timeframe,
                candles
        );
        this.scope = scope;
        this.priceVariationPrediction = Format.roundTwoDigits(priceVariationPrediction);
    }

    public PredictivePattern(PredictivePattern pattern) {
        super(
                pattern.getGranularity(),
                pattern.getSymbol(),
                pattern.getTimeframe(),
                pattern.getIntCandles()
        );
        this.scope = pattern.getScope();
        this.priceVariationPrediction = Format.roundTwoDigits(pattern.getPriceVariationPrediction());
    }
}

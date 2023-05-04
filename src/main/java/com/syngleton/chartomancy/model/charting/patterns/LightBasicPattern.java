package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.model.charting.candles.IntCandle;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
public class LightBasicPattern extends Pattern implements IntPattern {

    private final LocalDateTime startDate;
    @ToString.Exclude
    private final List<IntCandle> intCandles;

    public LightBasicPattern(
            List<IntCandle> intCandles,
            int granularity,
            int length,
            Symbol symbol,
            Timeframe timeframe,
            LocalDateTime startDate
    ) {
        super(
                PatternType.LIGHT_BASIC,
                granularity,
                length,
                symbol,
                timeframe
        );
        this.intCandles = intCandles;
        this.startDate = startDate;
    }
}

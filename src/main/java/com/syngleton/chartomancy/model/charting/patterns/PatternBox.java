package com.syngleton.chartomancy.model.charting.patterns;

import com.syngleton.chartomancy.model.charting.misc.ChartObject;
import com.syngleton.chartomancy.util.Check;
import lombok.Getter;

import java.util.*;

public class PatternBox extends ChartObject {

    @Getter
    private final Map<Integer, List<Pattern>> patterns;

    public PatternBox() {
        super();
        this.patterns = new TreeMap<>();
    }

    public PatternBox(ChartObject chartObject, List<Pattern> patterns) {

        super(chartObject);

        this.patterns = new TreeMap<>();

        if (Check.notNullNotEmpty(patterns)) {
            for (Pattern pattern : patterns) {
                if (pattern != null) {
                    int key = 0;
                    switch (pattern.getPatternType()) {
                        case BASIC -> key = ((pattern).getLength());
                        case PREDICTIVE -> key = ((PredictivePattern) pattern).getScope();
                        case TRADING -> key = ((TradingPattern) pattern).getScope();
                    }
                    this.patterns.computeIfAbsent(key, k -> new ArrayList<>());
                    this.patterns.get(key).add(pattern);
                }
            }
        }
    }

    public PatternBox(ChartObject chartObject, Map<Integer, List<Pattern>> patterns) {

        super(chartObject);

        this.patterns = patterns;
    }

    public List<Pattern> getListOfAllPatterns()   {

        List<Pattern> allPatterns = new ArrayList<>();

        for (Map.Entry<Integer, List<Pattern>> entry : this.patterns.entrySet()) {
            allPatterns.addAll(entry.getValue());
        }

        return allPatterns;
    }

    @Override
    public String toString()  {
        return "PatternBox{" +
                ", symbol=" + getSymbol() +
                ", timeframe=" + getTimeframe() + "}";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.matches((ChartObject) o);
    }

    @Override
    public int hashCode()  {
        int result = Objects.hash(getSymbol());
        result = 31 * result + Objects.hash(getTimeframe());
        return result;
    }
}

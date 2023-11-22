package co.syngleton.chartomancer.analytics.model;

import co.syngleton.chartomancer.global.tools.Check;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Log4j2
public final class PatternBox extends ChartObject {

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
                    if (Objects.requireNonNull(pattern.getPatternType()) == PatternType.PREDICTIVE) {
                        key = ((ComputablePattern) pattern).getScope();
                    } else {
                        key = ((pattern).getLength());
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

    public List<Pattern> getListOfAllPatterns() {

        List<Pattern> allPatterns = new ArrayList<>();

        for (Map.Entry<Integer, List<Pattern>> entry : this.patterns.entrySet()) {
            allPatterns.addAll(entry.getValue());
        }

        return allPatterns;
    }

    public int getMaxScope() {

        int maxScope = 0;
        List<Pattern> listOfAllPatterns = this.getListOfAllPatterns();

        if (Check.notNullNotEmpty(listOfAllPatterns)) {

            for (Pattern pattern : listOfAllPatterns) {
                maxScope = Math.max(maxScope, ((ScopedPattern) pattern).getScope());
            }
        }
        return maxScope;
    }

    public int getPatternLength() {

        int length = 0;
        List<Pattern> listOfAllPatterns = this.getListOfAllPatterns();

        if (Check.notNullNotEmpty(listOfAllPatterns)) {
            length = listOfAllPatterns.get(0).getLength();
        }

        return length;
    }

    @Override
    public String toString() {
        return "PatternBox{" +
                "symbol=" + getSymbol() +
                ", timeframe=" + getTimeframe() + "}";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.matches((ChartObject) o);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getSymbol());
        result = 31 * result + Objects.hash(getTimeframe());
        return result;
    }
}

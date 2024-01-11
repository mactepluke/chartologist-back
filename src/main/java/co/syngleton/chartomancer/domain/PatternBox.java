package co.syngleton.chartomancer.domain;

import co.syngleton.chartomancer.util.Check;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
@Getter
public final class PatternBox extends ChartObject {

    private final Map<Integer, List<Pattern>> patterns;

    public PatternBox(List<Pattern> patterns) {
        super(patterns == null || patterns.isEmpty() ? null : patterns.get(0));
        this.patterns = initializePatterns(patterns);
    }

    public PatternBox(ChartObject chartObject, List<Pattern> patterns) {
        super(chartObject);
        this.patterns = initializePatterns(patterns);
    }

    private static Map<Integer, List<Pattern>> initializePatterns(List<Pattern> patterns) {

        Map<Integer, List<Pattern>> patternsMap = new TreeMap<>();

        if (Check.isNotEmpty(patterns)) {
            for (Pattern pattern : patterns) {
                if (pattern != null) {
                    int key = getPatternKey(pattern);
                    patternsMap.computeIfAbsent(key, k -> new ArrayList<>());
                    patternsMap.get(key).add(pattern);
                }
            }
        }
        return patternsMap;
    }

    private static int getPatternKey(Pattern pattern) {
        return pattern instanceof PredictivePattern ? ((ScopedPattern) pattern).getScope() : pattern.getLength();
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

        if (Check.isNotEmpty(listOfAllPatterns)) {

            for (Pattern pattern : listOfAllPatterns) {
                maxScope = Math.max(maxScope, ((ScopedPattern) pattern).getScope());
            }
        }
        return maxScope;
    }

    public int getPatternLength() {

        int length = 0;
        List<Pattern> listOfAllPatterns = this.getListOfAllPatterns();

        if (Check.isNotEmpty(listOfAllPatterns)) {
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

package co.syngleton.chartomancer.core_entities;

import co.syngleton.chartomancer.util.Check;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public final class PatternBox extends ChartObject {

    private final Map<Integer, List<Pattern>> patterns;

    public PatternBox(List<Pattern> patterns) {
        super(patterns == null || patterns.isEmpty() ? null : patterns.get(0));
        this.patterns = splitPatternsByScope(patterns);
    }

    public PatternBox(CoreDataSnapshot.PatternBoxSnapShot patternBoxSnapShot) {
        super(patternBoxSnapShot.patterns().isEmpty() ? null :
                patternBoxSnapShot.patterns()
                        .entrySet()
                        .iterator()
                        .next()
                        .getValue()
                        .get(0));
        this.patterns = patternBoxSnapShot.patterns();
    }

    private static Map<Integer, List<Pattern>> splitPatternsByScope(List<Pattern> patterns) {

        Map<Integer, List<Pattern>> patternsMap = new HashMap<>();

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
        return pattern instanceof ScopedPattern scopedPattern ? scopedPattern.getScope() : pattern.getLength();
    }

    public Map<Integer, List<Pattern>> getPatterns() {
        return Collections.unmodifiableMap(this.patterns);
    }

    public void addPatterns(List<Pattern> patterns) {
        splitPatternsByScope(patterns).forEach((scope, patternsList) -> {
            if (this.patterns.containsKey(scope)) {
                this.patterns.get(scope).addAll(patternsList);
            } else {
                this.patterns.put(scope, patternsList);
            }
        });
    }

    public void putPatterns(List<Pattern> patterns) {
        this.patterns.clear();
        this.addPatterns(patterns);
    }


    public int getPatternsLength() {
        return this.patterns == null || this.patterns.isEmpty() ? 0 :
                this.patterns.entrySet().iterator().next().getValue().get(0).getLength();
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

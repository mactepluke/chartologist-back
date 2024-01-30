package co.syngleton.chartomancer.core_entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record CoreDataSnapshot(
        Set<Graph> graphs,
        Set<PatternBoxSnapShot> patternBoxes,
        Map<String, String> patternSettings,
        Set<PatternBoxSnapShot> tradingPatternBoxes,
        Map<String, String> tradingPatternSettings
) implements Serializable {
    public record PatternBoxSnapShot(
            Map<Integer, List<Pattern>> patterns
    ) implements Serializable {
    }
}

package co.syngleton.chartomancer.core_entities;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public record CoreDataSnapshot(
        Set<Graph> graphs,
        Set<PatternBox> patternBoxes,
        Map<String, String> patternSettings,
        Set<PatternBox> tradingPatternBoxes,
        Map<String, String> tradingPatternSettings
) implements Serializable {
}

package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.domain.CoreData;
import co.syngleton.chartomancer.domain.Pattern;

import java.util.List;

public interface PatternComputer {

    boolean computePatternBoxes(CoreData coreData, ComputationSettings.Builder settingsInput);

    List<Pattern> computePatterns(ComputationSettings.Builder settingsInput);

}

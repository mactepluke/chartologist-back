package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.Pattern;

import java.util.List;

public interface PatternComputer {

    boolean computeCoreData(CoreData coreData, ComputationSettings.Builder settingsInput);

    List<Pattern> computePatterns(ComputationSettings.Builder settingsInput);

}

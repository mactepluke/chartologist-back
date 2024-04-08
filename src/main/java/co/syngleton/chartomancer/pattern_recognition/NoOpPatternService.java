package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.Pattern;

import java.util.Collections;
import java.util.List;


/**
 * A no-op implementation of the PatternGenerator and PatternComputer interfaces.
 * This class is the only PatternGenerator and PatternComputer implementation that is made publicly available.
 * It is provided so the code can be compiled and run without any actual pattern recognition.
 * The default service class is deliberately hidden for legal and copyright reasons.
 */
final class NoOpPatternService implements PatternGenerator, PatternComputer {
    @Override
    public boolean computeCoreData(CoreData coreData, ComputationSettings.Builder settingsInput) {
        return false;
    }

    @Override
    public List<Pattern> computePatterns(ComputationSettings.Builder settingsInput) {
        return Collections.emptyList();
    }

    @Override
    public List<Pattern> createPatterns(PatternSettings.Builder settingsInput) {
        return Collections.emptyList();
    }
}

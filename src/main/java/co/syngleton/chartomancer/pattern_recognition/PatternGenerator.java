package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.shared_domain.CoreData;
import co.syngleton.chartomancer.shared_domain.Pattern;

import java.util.List;

public interface PatternGenerator {

    boolean createPatternBoxes(CoreData coreData, PatternSettings.Builder settingsInput);

    List<Pattern> createPatterns(PatternSettings.Builder settingsInput);

}

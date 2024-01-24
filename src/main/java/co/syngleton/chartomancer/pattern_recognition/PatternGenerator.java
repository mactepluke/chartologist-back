package co.syngleton.chartomancer.pattern_recognition;

import co.syngleton.chartomancer.shared_domain.Pattern;

import java.util.List;

public interface PatternGenerator {

    List<Pattern> createPatterns(PatternSettings.Builder settingsInput);

}

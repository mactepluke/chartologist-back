package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.pattern_recognition.ComputationSettings;
import co.syngleton.chartomancer.pattern_recognition.ComputationType;
import co.syngleton.chartomancer.pattern_recognition.PatternSettings;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "data", ignoreUnknownFields = false)
@Getter
@Setter
class DataProperties {
    private String folderName;
    private CoreDataSource source = CoreDataSource.UNKNOWN;
    private String sourceName;
    private List<String> filesNames;
    private boolean runAnalysisAtStartup;
    private boolean generateTradingData;
    private boolean createGraphsForMissingTimeframes;
    private boolean loadCoreDataAtStartup;
    private boolean overrideSavedCoreData;
    private boolean overrideSavedTestCoreData;
    private boolean createTimestampedCoreDataArchive;
    private PurgeOption purgeAfterTradingDataGeneration;
    private PatternSettings.Autoconfig patternSettingsAutoconfig;
    private ComputationSettings.Autoconfig computationSettingsAutoconfig;
    private ComputationType computationType;
    private PatternSettings.PatternType computablePatternType;
    private boolean atomicPartition;
    private boolean fullScope;
    private Set<Timeframe> patternBoxesTimeframes;

    enum CoreDataSource {
        SERIALIZED,
        UNKNOWN
    }
}

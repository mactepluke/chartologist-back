package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.pattern_recognition.ComputationSettings;
import co.syngleton.chartomancer.pattern_recognition.ComputationType;
import co.syngleton.chartomancer.pattern_recognition.PatternSettings;
import co.syngleton.chartomancer.core_entities.PurgeOption;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "data")
record DataProperties(
        @DefaultValue("data") String folderName,
        @DefaultValue("SERIALIZED") CoreDataSource source,
        @DefaultValue("data.ser") String sourceName,
        @DefaultValue("[]") List<String> filesNames,
        @DefaultValue("false") boolean runAnalysisAtStartup,
        @DefaultValue("false") boolean generateTradingData,
        @DefaultValue("false") boolean createGraphsForMissingTimeframes,
        @DefaultValue("true") boolean loadCoreDataAtStartup,
        @DefaultValue("false") boolean overrideSavedCoreData,
        @DefaultValue("false") boolean overrideSavedTestCoreData,
        @DefaultValue("false") boolean createTimestampedCoreDataArchive,
        @DefaultValue("NO") PurgeOption purgeAfterTradingDataGeneration,
        @DefaultValue("TIMEFRAME_LONG") PatternSettings.Autoconfig patternSettingsAutoconfig,
        @DefaultValue("DEFAULT") ComputationSettings.Autoconfig computationSettingsAutoconfig,
        @DefaultValue("BASIC_ITERATION") ComputationType computationType,
        @DefaultValue("PREDICTIVE") PatternSettings.PatternType computablePatternType,
        @DefaultValue("true") boolean atomicPartition,
        @DefaultValue("true") boolean fullScope,
        @DefaultValue("DAY,FOUR_HOUR") Set<Timeframe> patternBoxesTimeframes
) {

    enum CoreDataSource {
        SERIALIZED,
        UNKNOWN
    }
}

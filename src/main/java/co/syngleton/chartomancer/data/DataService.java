package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.charting.GraphGenerator;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.*;
import co.syngleton.chartomancer.pattern_recognition.PatternGenerator;
import co.syngleton.chartomancer.pattern_recognition.PatternSettings;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
@AllArgsConstructor
class DataService implements DataProcessor {
    private static final String PATH_DELIMITER = "/";
    private static final String PATH_ROOT = ".";

    private final GraphGenerator graphGenerator;
    private final PatternGenerator patternGenerator;
    private final CoreDataRepository coreDataRepository;
    private final DataProperties dataProperties;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createPatternsForCoreData(CoreData coreData, PatternSettings.Builder settingsInput) {

        if (isBroken(coreData)) {
            return false;
        }

        for (Graph graph : coreData.getUncomputedGraphs()) {

            if (dataProperties.patternBoxesTimeframes().contains(graph.getTimeframe())) {

                log.debug(">>> Creating patterns for graph: " + graph.getTimeframe() + ", " + graph.getSymbol());
                List<Pattern> patterns = patternGenerator.createPatterns(settingsInput.graph(graph));

                coreData.addPatterns(patterns);
            }
        }
        updateCoreDataPatternSettings(coreData, settingsInput.build());

        return coreData.getNumberOfPatternSets() > 0;
    }

    private boolean isBroken(CoreData coreData) {
        if (coreData == null || coreData.hasInvalidStructure()) {
            log.error("! Core data instance is null or broken !");
            return true;
        }
        return false;
    }

    private void updateCoreDataPatternSettings(@NonNull CoreData coreData, @NonNull PatternSettings patternSettings) {
        coreData.setPatternSetting(CoreDataSettingNames.PATTERN_GRANULARITY, patternSettings.getGranularity());
        coreData.setPatternSetting(CoreDataSettingNames.PATTERN_LENGTH, patternSettings.getLength());
        coreData.setPatternSetting(CoreDataSettingNames.SCOPE, patternSettings.getScope());
        coreData.setPatternSetting(CoreDataSettingNames.FULL_SCOPE, patternSettings.isFullScope());
        coreData.setPatternSetting(CoreDataSettingNames.ATOMIC_PARTITION, patternSettings.isAtomicPartition());
        coreData.setPatternSetting(CoreDataSettingNames.PATTERN_AUTOCONFIG, patternSettings.getAutoconfig());
        coreData.setPatternSetting(CoreDataSettingNames.COMPUTATION_PATTERN_TYPE, patternSettings.getPatternType());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean loadGraphs(CoreData coreData, String dataFolderName, List<String> dataFilesNames) {

        if (isBroken(coreData)) {
            return false;
        }

        boolean successfulComplete = true;

        for (String dataFileName : dataFilesNames) {

            String dataFilePath = PATH_ROOT + PATH_DELIMITER + dataFolderName + PATH_DELIMITER + dataFileName;

            log.info("Loading graph from: {}", dataFilePath);

            Graph graph = graphGenerator.generateGraphFromHistoricalDataSource(dataFilePath);

            if (graph == null) {
                successfulComplete = false;
            } else {
                coreData.addGraph(graph);
            }
        }
        return successfulComplete;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean loadCoreData(CoreData coreData, String dataSourceName) {

        if (isBroken(coreData)) {
            return false;
        }

        log.info("Loading core data from: {}, of name: {}", dataProperties.source(), dataSourceName);

        CoreDataSnapshot readData = coreDataRepository.loadCoreDataFrom(dataSourceName);

        if (readData != null) {
            coreData.mirror(DefaultCoreData.valueOf(readData));
            log.info("Loaded core data successfully.");
            return true;
        }
        log.error("Could not load core data.");
        return false;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean saveCoreData(CoreData coreData, String dataSourceName) {

        if (isBroken(coreData)) {
            return false;
        }

        log.info("Saving core data to: {}", dataSourceName);
        return coreDataRepository.saveCoreDataTo(coreData, dataSourceName);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean generateTradingData(CoreData coreData) {

        if (isBroken(coreData)) {
            return false;
        }

        return coreData.pushTradingPatternData();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createGraphsForMissingTimeframes(CoreData coreData) {

        if (isBroken(coreData)) {
            return false;
        }

        Timeframe lowestTimeframe = Timeframe.UNKNOWN;
        Graph lowestTimeframeGraph = null;

        Set<Timeframe> missingTimeframes = EnumSet.allOf(Timeframe.class);

        for (Graph graph : coreData.getReadOnlyGraphs()) {
            if (lowestTimeframe == Timeframe.UNKNOWN || graph.getTimeframe().durationInSeconds < lowestTimeframe.durationInSeconds) {
                lowestTimeframe = graph.getTimeframe();
                lowestTimeframeGraph = graph;
            }
            missingTimeframes.remove(graph.getTimeframe());
        }

        for (Timeframe timeframe : missingTimeframes) {
            if (lowestTimeframe.durationInSeconds < timeframe.durationInSeconds) {
                lowestTimeframeGraph = graphGenerator.upscaleToTimeFrame(lowestTimeframeGraph, timeframe);
                lowestTimeframe = timeframe;
                coreData.addGraph(lowestTimeframeGraph);
            }
        }
        return true;
    }

}


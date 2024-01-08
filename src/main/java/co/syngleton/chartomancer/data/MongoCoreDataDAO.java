package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.domain.CoreData;
import co.syngleton.chartomancer.domain.DefaultCoreData;
import co.syngleton.chartomancer.domain.Graph;
import co.syngleton.chartomancer.domain.PatternBox;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component("mongodb")
@AllArgsConstructor
@Log4j2
class MongoCoreDataDAO implements CoreDataDAO {
    private static final String GRAPHS_DATA_SUFFIX = ".GRAPHS";
    private static final String PATTERNS_DATA_SUFFIX = ".PATTERNS";
    private static final String TRADING_PATTERNS_DATA_SUFFIX = ".TRADING_PATTERNS";
    private static final String DEFAULT_DATA_SOURCE_NAME = "data.ser";
    private static String graphsSourceName;
    private static String patternsSourceName;
    private static String tradingPatternsSourceName;
    private static GraphsMongoDTO graphsMongoDTO;
    private static PatternBoxesMongoDTO patternBoxesMongoDTO;
    private static PatternBoxesMongoDTO tradingPatternBoxesMongoDTO;
    private final GraphsDataMongoRepository graphsDataMongoRepository;
    private final PatternsDataMongoRepository patternsDataMongoRepository;

    private static void initializeSourceNames(String dataSourceName) {

        dataSourceName = streamlineDataSourceName(dataSourceName);

        graphsSourceName = dataSourceName + GRAPHS_DATA_SUFFIX;
        patternsSourceName = dataSourceName + PATTERNS_DATA_SUFFIX;
        tradingPatternsSourceName = dataSourceName + TRADING_PATTERNS_DATA_SUFFIX;
    }

    private static String streamlineDataSourceName(String dataSourceName) {
        if (dataSourceName == null || dataSourceName.isEmpty()) {
            dataSourceName = DEFAULT_DATA_SOURCE_NAME;
        }
        return dataSourceName.trim();
    }

    private static void initializeMongoDTOs(CoreData coreData) {
        graphsMongoDTO = new GraphsMongoDTO(graphsSourceName, coreData.getGraphs());
        patternBoxesMongoDTO = new PatternBoxesMongoDTO(patternsSourceName, coreData.getPatternBoxes(), coreData.getPatternSettings());
        tradingPatternBoxesMongoDTO = new PatternBoxesMongoDTO(tradingPatternsSourceName, coreData.getTradingPatternBoxes(), coreData.getTradingPatternSettings());
    }

    @Override
    @Transactional(readOnly = true)
    public CoreData loadCoreDataFrom(String dataSourceName) {

        initializeSourceNames(dataSourceName);

        return reconstituteCoreData(loadGraphs(),
                loadPatternBoxesAndSettings(patternsSourceName),
                loadPatternBoxesAndSettings(tradingPatternsSourceName));
    }

    private CoreData reconstituteCoreData(GraphsMongoDTO graphsMongoDTO,
                                          PatternBoxesMongoDTO patternBoxesMongoDTO,
                                          PatternBoxesMongoDTO tradingPatternBoxesMongoDTO) {

        CoreData coreData = new DefaultCoreData();

        coreData.setGraphs(retrieveGraphs(graphsMongoDTO));
        coreData.setPatternBoxes(retrievePatternBoxes(patternBoxesMongoDTO));
        coreData.setPatternSettings(retrievePatternSettings(patternBoxesMongoDTO));
        coreData.setTradingPatternBoxes(retrievePatternBoxes(tradingPatternBoxesMongoDTO));
        coreData.setTradingPatternSettings(retrievePatternSettings(tradingPatternBoxesMongoDTO));

        return coreData;
    }

    private GraphsMongoDTO loadGraphs() {
        return graphsDataMongoRepository.findById(graphsSourceName).orElse(null);
    }

    private PatternBoxesMongoDTO loadPatternBoxesAndSettings(String patternsSourceName) {
        return patternsDataMongoRepository.findById(patternsSourceName).orElse(null);
    }

    private Set<Graph> retrieveGraphs(GraphsMongoDTO graphsMongoDTO) {

        if (graphsMongoDTO == null || graphsMongoDTO.getGraphs() == null || graphsMongoDTO.getGraphs().isEmpty()) {
            log.warn("No graphs found in {}.", graphsSourceName);
            return Collections.emptySet();
        } else {
            return graphsMongoDTO.getGraphs();
        }
    }

    private Set<PatternBox> retrievePatternBoxes(PatternBoxesMongoDTO patternBoxesMongoDTO) {

        if (patternBoxesMongoDTO == null || patternBoxesMongoDTO.getPatternBoxes() == null || patternBoxesMongoDTO.getPatternBoxes().isEmpty()) {
            log.warn("No patterns found.");
            return Collections.emptySet();
        } else {
            return patternBoxesMongoDTO.getPatternBoxes();
        }
    }

    private Map<String, String> retrievePatternSettings(PatternBoxesMongoDTO patternBoxesMongoDTO) {

        if (patternBoxesMongoDTO == null || patternBoxesMongoDTO.getPatternSettings() == null) {
            log.warn("No pattern settings found.");
            return new HashMap<>();
        } else {
            return patternBoxesMongoDTO.getPatternSettings();
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean saveCoreDataTo(CoreData coreData, String dataSourceName) {

        validateParameters(coreData, dataSourceName);
        initializeSourceNames(dataSourceName);
        initializeMongoDTOs(coreData);
        deletePreviouslySavedCoreData();
        saveGraphs();
        savePatternBoxesAndSettings(patternBoxesMongoDTO);
        savePatternBoxesAndSettings(tradingPatternBoxesMongoDTO);

        return logAndAssessSaveCoreDataResult();
    }


    private void validateParameters(CoreData coreData, String dataSourceName) {
        if (coreData == null || dataSourceName == null || dataSourceName.isEmpty()) {
            throw new IllegalArgumentException("Core data cannot be null and data source name cannot be null or empty.");
        }
    }

    private void deletePreviouslySavedCoreData() {
        graphsDataMongoRepository.deleteById(graphsSourceName);
        graphsDataMongoRepository.deleteById(patternsSourceName);
        graphsDataMongoRepository.deleteById(tradingPatternsSourceName);
    }

    private void saveGraphs() {
        if (graphsMongoDTO.getGraphs() == null || graphsMongoDTO.getGraphs().isEmpty()) {
            log.warn("No graphs to save to {}.", graphsMongoDTO.getId());
        } else {
            graphsDataMongoRepository.insert(graphsMongoDTO);
        }
    }

    private void savePatternBoxesAndSettings(PatternBoxesMongoDTO patternBoxesMongoDTO) {
        if (patternBoxesMongoDTO.getPatternBoxes() == null || patternBoxesMongoDTO.getPatternBoxes().isEmpty()) {
            log.warn("No patterns to save to {}.", patternBoxesMongoDTO.getId());
        } else {
            patternsDataMongoRepository.insert(patternBoxesMongoDTO);
        }
    }

    private boolean logAndAssessSaveCoreDataResult() {
        if (checkSavedCoreDataIntegrity()) {
            log.info("Core data fully saved to MongoDB.");
            return true;
        } else {
            log.warn("Core data has not been fully saved to MongoDB as some components are missing.");
            return false;
        }
    }

    private boolean checkSavedCoreDataIntegrity() {
        return graphsDataMongoRepository.findById(graphsSourceName).isPresent()
                && patternsDataMongoRepository.findById(patternsSourceName).isPresent()
                && patternsDataMongoRepository.findById(tradingPatternsSourceName).isPresent();
    }
}

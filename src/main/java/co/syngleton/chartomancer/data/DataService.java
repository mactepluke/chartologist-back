package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.charting.GraphGenerator;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.pattern_recognition.PatternGenerator;
import co.syngleton.chartomancer.pattern_recognition.PatternSettings;
import co.syngleton.chartomancer.shared_constants.CoreDataSettingNames;
import co.syngleton.chartomancer.shared_domain.*;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j2
@Service
@AllArgsConstructor
class DataService implements DataProcessor {
    private static final String NEW_LINE = System.lineSeparator();

    private final GraphGenerator graphGenerator;
    private final PatternGenerator patternGenerator;
    private final CoreDataDAO coreDataDAO;
    private final DataProperties dataProperties;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createPatternBoxes(CoreData coreData, PatternSettings.Builder settingsInput) {

        Set<PatternBox> patternBoxes = new HashSet<>();

        if (coreData != null
                && Check.isNotEmpty(coreData.getGraphs())
        ) {
            if (Check.isNotEmpty(coreData.getPatternBoxes())) {
                patternBoxes = coreData.getPatternBoxes();
            }
            for (Graph graph : coreData.getGraphs()) {

                if (graph.doesNotMatchAnyChartObjectIn(patternBoxes) && dataProperties.getPatternBoxesTimeframes().contains(graph.getTimeframe())) {

                    log.debug(">>> Creating patterns for graph: " + graph.getTimeframe() + " " + graph.getSymbol());
                    List<Pattern> patterns = patternGenerator.createPatterns(settingsInput.graph(graph));

                    if (Check.isNotEmpty(patterns)) {
                        patternBoxes.add(new PatternBox(patterns));
                    }
                }
            }
            coreData.setPatternBoxes(patternBoxes);
            updateCoreDataPatternSettings(coreData, settingsInput.build());
        }
        return !patternBoxes.isEmpty();
    }

    private void updateCoreDataPatternSettings(@NonNull CoreData coreData, @NonNull PatternSettings patternSettings) {
        coreData.setPatternSetting(CoreDataSettingNames.PATTERN_GRANULARITY, Integer.toString(patternSettings.getGranularity()));
        coreData.setPatternSetting(CoreDataSettingNames.PATTERN_LENGTH, Integer.toString(patternSettings.getLength()));
        coreData.setPatternSetting(CoreDataSettingNames.SCOPE, Integer.toString(patternSettings.getScope()));
        coreData.setPatternSetting(CoreDataSettingNames.FULL_SCOPE, Boolean.toString(patternSettings.isFullScope()));
        coreData.setPatternSetting(CoreDataSettingNames.ATOMIC_PARTITION, Boolean.toString(patternSettings.isAtomicPartition()));
        coreData.setPatternSetting(CoreDataSettingNames.PATTERN_AUTOCONFIG, patternSettings.getAutoconfig().toString());
        coreData.setPatternSetting(CoreDataSettingNames.COMPUTATION_PATTERN_TYPE, patternSettings.getPatternType().toString());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean loadGraphs(CoreData coreData, String dataFolderName, List<String> dataFilesNames) {

        Set<Graph> graphs = new HashSet<>();

        if (coreData != null) {
            for (String dataFileName : dataFilesNames) {

                String dataFilePath = "./" + dataFolderName + "/" + dataFileName;

                log.info("Loading graph from: {}", dataFilePath);

                Graph graph = graphGenerator.generateContinuousGraphFromFile(dataFilePath);

                if (graph != null && graph.doesNotMatchAnyChartObjectIn(coreData.getGraphs())) {
                    graphs.add(graph);
                }
            }
            coreData.getGraphs().addAll(graphs);
        }
        return !graphs.isEmpty();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean loadCoreData(CoreData coreData, String dataSourceName) {

        log.info("Loading core data from: {}, of name: {}", dataProperties.getSource(), dataSourceName);

        CoreData readData = coreDataDAO.loadCoreDataFrom(dataSourceName);

        if (readData != null) {
            coreData.copy(readData);
            log.info("Loaded core data successfully.");
            return true;
        }
        log.error("Could not load core data.");
        return false;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean saveCoreData(CoreData coreData, String dataFileName) {

        log.info("Saving core data to: {}", dataProperties.getSource() + "/" + dataFileName);
        return coreDataDAO.saveCoreDataTo(coreData, dataFileName);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean generateTradingData(CoreData coreData) {

        Set<PatternBox> tradingData = new HashSet<>();

        if (coreData == null || Check.isEmpty(coreData.getPatternBoxes())) {
            return false;
        }

        for (PatternBox patternBox : coreData.getPatternBoxes()) {

            List<Pattern> tradingPatterns = new ArrayList<>();

            if (Check.isNotEmpty(patternBox.getPatterns())) {
                tradingPatterns = convertPatternsToTrading(patternBox.getPatterns());
            }

            if (Check.isNotEmpty(tradingPatterns)) {
                tradingData.add(new PatternBox(tradingPatterns));
            }
        }

        coreData.pushTradingPatternData(tradingData);

        return !tradingData.isEmpty();
    }

    private List<Pattern> convertPatternsToTrading(Map<Integer, List<Pattern>> patterns) {

        if (Check.isEmpty(patterns)) {
            throw new InvalidParametersException("Cannot convert empty patterns.");
        }

        List<Pattern> tradingPatternsList = new ArrayList<>();

        for (Map.Entry<Integer, List<Pattern>> entry : patterns.entrySet()) {

            for (Pattern pattern : entry.getValue()) {

                if (pattern instanceof PredictivePattern predictivePattern) {
                    tradingPatternsList.add(new TradingPattern(predictivePattern));
                }
            }
        }
        return tradingPatternsList;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean purgeUselessData(CoreData coreData, PurgeOption option) {

        if (option == null) {
            return false;
        }

        switch (option) {
            case GRAPHS -> coreData.setGraphs(null);
            case PATTERNS -> coreData.setPatternBoxes(null);
            case GRAPHS_AND_PATTERNS -> coreData.purgeNonTrading();
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean createGraphsForMissingTimeframes(CoreData coreData) {

        if (coreData == null || Check.isEmpty(coreData.getGraphs())) {
            return false;
        }

        Timeframe lowestTimeframe = Timeframe.UNKNOWN;
        Graph lowestTimeframeGraph = null;

        Set<Timeframe> missingTimeframes = new TreeSet<>(List.of(Timeframe.SECOND,
                Timeframe.MINUTE,
                Timeframe.HALF_HOUR,
                Timeframe.HOUR,
                Timeframe.FOUR_HOUR,
                Timeframe.DAY,
                Timeframe.WEEK));

        for (Graph graph : coreData.getGraphs()) {
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
                coreData.getGraphs().add(lowestTimeframeGraph);
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public void printCoreData(CoreData coreData) {

        if (coreData != null) {

            String coreDataToPrint = NEW_LINE + "*** CORE DATA ***" + generateGraphsToPrint(coreData.getGraphs()) + generatePatternBoxesToPrint(coreData.getPatternBoxes(), "PATTERN") + generatePatternBoxesToPrint(coreData.getTradingPatternBoxes(), "TRADING PATTERN") + generateMemoryUsageToPrint();

            log.info(coreDataToPrint);
        } else {
            log.info("Cannot print core data: object is empty.");
        }
    }

    private @NonNull String generateGraphsToPrint(Set<Graph> graphs) {

        StringBuilder graphsBuilder = new StringBuilder();

        if (Check.isNotEmpty(graphs)) {
            graphsBuilder.append(NEW_LINE).append(graphs.size()).append(" GRAPH(S)").append(NEW_LINE);
            for (Graph graph : graphs) {
                graphsBuilder.append("-> ").append(graph.getName()).append(", ").append(graph.getSymbol()).append(", ").append(graph.getTimeframe()).append(", ").append(graph.getFloatCandles().size()).append(" candles").append(NEW_LINE);
            }
        } else {
            graphsBuilder.append(NEW_LINE).append("0 GRAPH(S)").append(NEW_LINE).append("***");
        }
        return graphsBuilder.toString();
    }

    private @NonNull String generatePatternBoxesToPrint(Set<PatternBox> patternBoxes, String nameOfContent) {

        StringBuilder patternBoxesBuilder = new StringBuilder();

        if (Check.isNotEmpty(patternBoxes)) {
            patternBoxesBuilder.append(NEW_LINE).append(patternBoxes.size()).append(" ").append(nameOfContent).append(" BOX(ES)").append(NEW_LINE);
            for (PatternBox patternBox : patternBoxes) {

                for (Map.Entry<Integer, List<Pattern>> entry : patternBox.getPatterns().entrySet()) {

                    if (entry.getValue() != null) {

                        Pattern anyPattern = patternBox.getPatterns().entrySet().iterator().next().getValue().get(0);

                        patternBoxesBuilder.append("-> ").append(entry.getValue().size()).append(" patterns, ").append(patternBox.getSymbol()).append(", ").append(patternBox.getTimeframe()).append(", pattern scope=").append(entry.getKey()).append(", pattern type=").append(anyPattern.getClass()).append(", pattern length=").append(anyPattern.getLength()).append(", pattern granularity=").append(anyPattern.getGranularity()).append(NEW_LINE);
                    }

                }
                patternBoxesBuilder.append("***").append(NEW_LINE);
            }
        } else {
            patternBoxesBuilder.append(NEW_LINE).append("0").append(" ").append(nameOfContent).append(" BOX(ES)").append(NEW_LINE).append("***");
        }
        return patternBoxesBuilder.toString();
    }

    private @NonNull String generateMemoryUsageToPrint() {

        return NEW_LINE + "Current heap size (MB): " + Format.roundAccordingly((float) Runtime.getRuntime().totalMemory() / 1000000) + NEW_LINE + "Max heap size (MB): " + Format.roundAccordingly((float) Runtime.getRuntime().maxMemory() / 1000000) + NEW_LINE + "Free heap size (MB): " + Format.roundAccordingly((float) Runtime.getRuntime().freeMemory() / 1000000) + NEW_LINE;
    }

}


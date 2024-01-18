package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.charting.GraphGenerator;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.shared_domain.*;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

import static co.syngleton.chartomancer.shared_constants.Misc.NEW_LINE;

@Log4j2
@Service
class DataService implements ApplicationContextAware, DataProcessor {
    private final GraphGenerator graphGenerator;
    private final String dataSource;
    private CoreDataDAO coreDataDAO;
    private ApplicationContext applicationContext;
    @Value("${data_source_name:data.ser}")
    private String dataSourceName;

    @Autowired
    DataService(@Value("${data_source:serialized}") String dataSource,
                GraphGenerator graphGenerator) {
        this.dataSource = dataSource;
        this.graphGenerator = graphGenerator;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void init() {
        log.debug("Using data source: {}", dataSource);
        this.coreDataDAO = getCoreDataDAO(dataSource);
    }

    private CoreDataDAO getCoreDataDAO(String dataSource) {
        return applicationContext.getBean(dataSource, CoreDataDAO.class);
    }

    @Override
    public boolean loadGraphs(CoreData coreData, String dataFolderName, List<String> dataFilesNames) {

        Set<Graph> graphs = new HashSet<>();

        if (coreData != null) {
            for (String dataFileName : dataFilesNames) {

                String dataFilePath = "./" + dataFolderName + "/" + dataFileName;

                log.info("Loading graph from: {}", dataFilePath);

                Graph graph = graphGenerator.generateGraphFromFile(dataFilePath);

                if (graph != null && graph.doesNotMatchAnyChartObjectIn(coreData.getGraphs())) {
                    graphs.add(graph);
                }
            }
            coreData.getGraphs().addAll(graphs);
        }
        return !graphs.isEmpty();
    }

    @Override
    public boolean loadCoreData(CoreData coreData) {
        return loadCoreDataWithName(coreData, dataSourceName);
    }

    @Override
    public boolean loadCoreDataWithName(CoreData coreData, String dataSourceName) {

        log.info("Loading core data from: {}, of name: {}", dataSource, dataSourceName);

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
    public boolean saveCoreData(CoreData coreData) {
        return saveCoreDataWithName(coreData, dataSourceName);
    }

    @Override
    public boolean saveCoreDataWithName(CoreData coreData, String dataFileName) {

        log.info("Saving core data to: {}", dataSource + "/" + dataFileName);
        return coreDataDAO.saveCoreDataTo(coreData, dataFileName);
    }

    @Override
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
    public boolean purgeNonTradingData(CoreData coreData, PurgeOption option) {

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


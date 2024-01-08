package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.analytics.misc.PurgeOption;
import co.syngleton.chartomancer.charting.GraphGenerator;
import co.syngleton.chartomancer.domain.*;
import co.syngleton.chartomancer.global.tools.Check;
import co.syngleton.chartomancer.global.tools.Format;
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

@Log4j2
@Service
public class CoreDataInteractor implements ApplicationContextAware {
    //TODO: split methods in interfaces
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final GraphGenerator graphGenerator;
    private final String dataSource;
    private ApplicationContext applicationContext;
    private CoreDataDAO coreDataDAO;

    @Value("${data_source_name:data.ser}")
    private String dataSourceName;

    @Autowired
    public CoreDataInteractor(@Value("${data_source:serialized}") String dataSource,
                              GraphGenerator graphGenerator) {
        this.graphGenerator = graphGenerator;
        this.dataSource = dataSource;
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

    public boolean loadGraphs(CoreData coreData, String dataFolderName, List<String> dataFilesNames) {

        Set<Graph> graphs = new HashSet<>();

        if (coreData != null) {
            for (String dataFileName : dataFilesNames) {

                Graph graph = graphGenerator.generateGraphFromFile("./" + dataFolderName + "/" + dataFileName);

                if (graph != null && graph.doesNotMatchAnyChartObjectIn(coreData.getGraphs())) {
                    graphs.add(graph);
                }
            }
            if (!Check.notNullNotEmpty(coreData.getGraphs())) {
                coreData.setGraphs(graphs);
            } else {
                coreData.getGraphs().addAll(graphs);
            }
        }
        return !graphs.isEmpty();
    }

    public boolean loadCoreData(CoreData coreData) {
        return loadCoreDataWithName(coreData, dataSourceName);
    }

    public boolean loadCoreDataWithName(CoreData coreData, String dataSourceName) {

        log.info("> Loading core data from: {}", dataSource);

        CoreData readData = coreDataDAO.loadCoreDataFrom(dataSourceName);

        if (readData != null) {
            coreData.copy(readData);
            log.info("> Loaded core data successfully.");
            return true;
        }
        return false;
    }

    public boolean saveCoreData(CoreData coreData) {
        return saveCoreDataWithName(coreData, dataSourceName);
    }

    public boolean saveCoreDataWithName(CoreData coreData, String dataFileName) {

        log.info("> Saving core data to: {}", dataSource + "/" + dataFileName);
        return coreDataDAO.saveCoreDataTo(coreData, dataFileName);
    }

    public boolean generateTradingData(CoreData coreData) {

        Set<PatternBox> tradingData = new HashSet<>();

        if ((coreData != null) && Check.notNullNotEmpty(coreData.getPatternBoxes())) {

            for (PatternBox patternBox : coreData.getPatternBoxes()) {

                Map<Integer, List<Pattern>> tradingPatterns = new TreeMap<>();

                List<Pattern> anyPatternList = null;

                if (Check.notNullNotEmpty(patternBox.getPatterns())) {
                    tradingPatterns = convertPatternsToTrading(patternBox.getPatterns());
                    anyPatternList = tradingPatterns.entrySet().iterator().next().getValue();
                }

                if (Check.notNullNotEmpty(anyPatternList) && !tradingPatterns.isEmpty() && !anyPatternList.isEmpty()) {
                    tradingData.add(new PatternBox(anyPatternList.get(0), tradingPatterns));
                }
            }
            coreData.pushTradingPatternData(tradingData);
        }
        return !tradingData.isEmpty();
    }

    private Map<Integer, List<Pattern>> convertPatternsToTrading(Map<Integer, List<Pattern>> patterns) {

        Map<Integer, List<Pattern>> tradingPatterns = new TreeMap<>();

        if (Check.notNullNotEmpty(patterns)) {

            for (Map.Entry<Integer, List<Pattern>> entry : patterns.entrySet()) {

                List<Pattern> tradingPatternsList = new ArrayList<>();

                for (Pattern pattern : entry.getValue()) {

                    if (pattern instanceof PredictivePattern) {
                        tradingPatternsList.add(new TradingPattern((PredictivePattern) pattern));
                    } else {
                        return patterns;
                    }
                }
                tradingPatterns.put(entry.getKey(), tradingPatternsList);
            }
        }
        return tradingPatterns;
    }

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

    public boolean createGraphsForMissingTimeframes(CoreData coreData) {

        if (coreData != null && Check.notNullNotEmpty(coreData.getGraphs())) {

            Timeframe lowestTimeframe = Timeframe.UNKNOWN;
            Graph lowestTimeframeGraph = null;

            Set<Timeframe> missingTimeframes = new TreeSet<>(List.of(Timeframe.SECOND, Timeframe.MINUTE, Timeframe.HALF_HOUR, Timeframe.HOUR, Timeframe.FOUR_HOUR, Timeframe.DAY, Timeframe.WEEK));

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
        return false;
    }

    public boolean printCoreData(CoreData coreData) {

        if (coreData != null) {

            String coreDataToPrint = NEW_LINE + "*** CORE DATA ***" + generateGraphsToPrint(coreData.getGraphs()) + generatePatternBoxesToPrint(coreData.getPatternBoxes(), "PATTERN") + generatePatternBoxesToPrint(coreData.getTradingPatternBoxes(), "TRADING PATTERN") + generateMemoryUsageToPrint();

            log.info(coreDataToPrint);
            return true;
        } else {
            log.info("Cannot print core data: object is empty.");
            return false;
        }
    }

    private @NonNull String generateGraphsToPrint(Set<Graph> graphs) {

        StringBuilder graphsBuilder = new StringBuilder();

        if (Check.notNullNotEmpty(graphs)) {
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

        if (Check.notNullNotEmpty(patternBoxes)) {
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


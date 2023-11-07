package co.syngleton.chartomancer.analytics.service;

import co.syngleton.chartomancer.analytics.dao.CoreDataDAO;
import co.syngleton.chartomancer.analytics.data.CoreData;
import co.syngleton.chartomancer.analytics.factory.GraphFactory;
import co.syngleton.chartomancer.analytics.misc.CSVFormat;
import co.syngleton.chartomancer.analytics.misc.PurgeOption;
import co.syngleton.chartomancer.analytics.model.FloatCandle;
import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.ObsoletePredictivePattern;
import co.syngleton.chartomancer.analytics.model.ObsoleteTradingPattern;
import co.syngleton.chartomancer.analytics.model.Pattern;
import co.syngleton.chartomancer.analytics.model.PatternBox;
import co.syngleton.chartomancer.analytics.model.PredictivePattern;
import co.syngleton.chartomancer.analytics.model.Timeframe;
import co.syngleton.chartomancer.analytics.model.TradingPattern;
import co.syngleton.chartomancer.global.exceptions.InvalidParametersException;
import co.syngleton.chartomancer.global.tools.Check;
import co.syngleton.chartomancer.global.tools.Format;
import co.syngleton.chartomancer.global.tools.datatabletool.DataTableTool;
import co.syngleton.chartomancer.global.tools.datatabletool.PrintableDataTable;
import co.syngleton.chartomancer.signaling.misc.ExternalDataSource;
import co.syngleton.chartomancer.signaling.service.ExternalDataSourceService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@Log4j2
@Service
public class DataService {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String DEFAULT_DATA_SOURCE_NAME = "./core_data/data.ser";
    private final GraphFactory graphFactory;
    private final ExternalDataSourceService cryptoCompareService;
    private final CoreDataDAO coreDataDAO;
    @Value("${reading_attempts:3}")
    private int readingAttempts;
    @Value("${external_data_source}")
    private ExternalDataSource externalDataSource;

    //TODO: add support for multiple data sources
    @Autowired
    public DataService(GraphFactory graphFactory, ExternalDataSourceService cryptoCompareService, @Qualifier("serialized") CoreDataDAO coreDataDAO) {
        this.graphFactory = graphFactory;
        this.cryptoCompareService = cryptoCompareService;
        this.coreDataDAO = coreDataDAO;
    }

    public void setExternalDataSource(ExternalDataSource externalDataSource) {
        this.externalDataSource = externalDataSource;
    }

    public ExternalDataSourceService getExternalDataSourceService() {
        if (Objects.requireNonNull(externalDataSource) == ExternalDataSource.CRYPTO_COMPARE) {
            return cryptoCompareService;
        }
        throw new InvalidParametersException("Data source is missing: check 'external_data_source' in properties file and make sure it is set.");
    }

    public boolean writeCsvFile(String fileName, PrintableDataTable content) {
        return DataTableTool.writeDataTableToFile(fileName, content);
    }

    public boolean loadGraphs(CoreData coreData, String dataFolderName, List<String> dataFilesNames) {

        Set<Graph> graphs = new HashSet<>();

        if (coreData != null) {
            for (String dataFileName : dataFilesNames) {

                Graph graph = loadGraph("./" + dataFolderName + "/" + dataFileName);

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

    public Graph loadGraph(String path) {

        Graph graph;

        log.info("Reading file... : " + path);

        CSVFormat currentFormat = readFileFormat(path);

        if (currentFormat != null) {
            graph = graphFactory.create(path, currentFormat);
            log.debug("*** CREATED GRAPH (name: {}, symbol: {}, timeframe: {}) ***", graph.getName(), graph.getSymbol(), graph.getTimeframe());
            return graph;
        } else {
            log.error("File format header not found (parsed the first {} lines without success). List of supported headers:", readingAttempts);

            for (CSVFormat csvReader : CSVFormat.values()) {
                log.info("Format: {}, header: \"{}\"", csvReader, csvReader.formatHeader);
            }
            return null;
        }
    }

    private CSVFormat readFileFormat(String path) {

        CSVFormat csvFormat = null;
        String line;
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            do {
                line = reader.readLine();
                if (line != null) {
                    count++;
                    for (CSVFormat format : CSVFormat.values()) {

                        if (line.matches(format.formatHeader)) {
                            csvFormat = format;
                        }
                    }
                }
            } while (line != null && count < readingAttempts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFormat;
    }

    public boolean printGraph(Graph graph) {

        if (graph != null) {
            log.info("*** PRINTING GRAPH (name: {}, symbol: {}, timeframe: {}) ***", graph.getName(), graph.getSymbol(), graph.getTimeframe());
            int i = 1;
            for (FloatCandle floatCandle : graph.getFloatCandles()) {
                log.info("{} -> {}", i++, floatCandle.toString());
            }
            return true;
        } else {
            log.info("Cannot print graph: no data have been loaded.");
            return false;
        }
    }

    public boolean loadCoreData(CoreData coreData) {
        return loadCoreDataWithName(coreData, DEFAULT_DATA_SOURCE_NAME);
    }

    public boolean loadCoreDataWithName(CoreData coreData, String dataSourceName) {

        log.info("> Loading core data from: {}", "serialized");

        CoreData readData = coreDataDAO.loadCoreDataWithName(dataSourceName);

        if (readData != null) {
            coreData.copy(readData);
            log.info("> Loaded core data successfully.");
            return true;
        }
        return false;
    }

    public boolean saveCoreData(CoreData coreData) {
        return saveCoreDataWithName(coreData, DEFAULT_DATA_SOURCE_NAME);
    }

    public boolean saveCoreDataWithName(CoreData coreData, String dataFileName) {

        log.info("> Saving core data to: {}", "serialized");
        return coreDataDAO.saveCoreDataWithName(coreData, dataFileName);
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

                if (Check.notNullNotEmpty(anyPatternList) && !tradingPatterns.isEmpty() && anyPatternList.get(0) != null) {
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

                    switch (pattern.getPatternType()) {
                        case PREDICTIVE_OBSOLETE ->
                                tradingPatternsList.add(new ObsoleteTradingPattern((ObsoletePredictivePattern) pattern));
                        case PREDICTIVE -> tradingPatternsList.add(new TradingPattern((PredictivePattern) pattern));
                        default -> {
                            return patterns;
                        }
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

    public void purgeAllData(CoreData coreData) {
        if (coreData != null) {
            coreData.purgeAll();
        }
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
                    lowestTimeframeGraph = graphFactory.upscaleTimeframe(lowestTimeframeGraph, timeframe);
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

                        patternBoxesBuilder.append("-> ").append(entry.getValue().size()).append(" patterns, ").append(patternBox.getSymbol()).append(", ").append(patternBox.getTimeframe()).append(", pattern scope=").append(entry.getKey()).append(", pattern type=").append(anyPattern.getPatternType()).append(", pattern length=").append(anyPattern.getLength()).append(", pattern granularity=").append(anyPattern.getGranularity()).append(NEW_LINE);
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


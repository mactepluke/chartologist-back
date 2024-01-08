package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.analytics.computation.ComputationSettings;
import co.syngleton.chartomancer.analytics.computation.ComputationType;
import co.syngleton.chartomancer.analytics.factory.PatternSettings;
import co.syngleton.chartomancer.analytics.misc.PurgeOption;
import co.syngleton.chartomancer.analytics.service.PatternComputingService;
import co.syngleton.chartomancer.charting.GraphGenerator;
import co.syngleton.chartomancer.domain.*;
import co.syngleton.chartomancer.global.service.LaunchService;
import co.syngleton.chartomancer.global.tools.Check;
import co.syngleton.chartomancer.global.tools.Format;
import co.syngleton.chartomancer.trading.service.TradingService;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class DataService implements ApplicationContextAware, DataProcessor, DataInitializer {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String CORE_DATA_ARCHIVES_FOLDER_PATH = "./archives/Core_Data_archive_";
    private final GraphGenerator graphGenerator;
    private final LaunchService launchService;
    private final PatternComputingService patternComputingService;
    private final TradingService tradingService;
    private final String dataSource;
    private CoreDataDAO coreDataDAO;
    private ApplicationContext applicationContext;
    @Value("${data_source_name:data.ser}")
    private String dataSourceName;

    @Autowired
    public DataService(@Value("${data_source:serialized}") String dataSource,
                       GraphGenerator graphGenerator,
                       PatternComputingService patternComputingService,
                       LaunchService launchService,
                       TradingService tradingService) {
        this.graphGenerator = graphGenerator;
        this.dataSource = dataSource;
        this.patternComputingService = patternComputingService;
        this.launchService = launchService;
        this.tradingService = tradingService;
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

    @Override
    public boolean loadCoreData(CoreData coreData) {
        return loadCoreDataWithName(coreData, dataSourceName);
    }

    @Override
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

    @Override
    public boolean saveCoreData(CoreData coreData) {
        return saveCoreDataWithName(coreData, dataSourceName);
    }

    @Override
    public boolean saveCoreDataWithName(CoreData coreData, String dataFileName) {

        log.info("> Saving core data to: {}", dataSource + "/" + dataFileName);
        return coreDataDAO.saveCoreDataTo(coreData, dataFileName);
    }

    @Override
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

    @Override
    public CoreData initializeCoreData(String dataFolderName,
                                       List<String> dataFilesNames,
                                       boolean runAnalysisAtStartup,
                                       boolean generateTradingData,
                                       boolean createGraphsForMissingTimeframes,
                                       boolean loadCoreDataAtStartup,
                                       boolean overrideSavedCoreData,
                                       boolean overrideSavedTestCoreData,
                                       boolean createTimestampedCoreDataArchive,
                                       PurgeOption purgeAfterTradingDataGeneration,
                                       PatternSettings.Autoconfig patternSettingsAutoconfig,
                                       ComputationSettings.Autoconfig computationSettings,
                                       ComputationType computationType,
                                       PatternSettings.PatternType computablePatternType,
                                       boolean atomicPartition,
                                       boolean fullScope,
                                       boolean launchAutomation
    ) {

        CoreData coreData = new DefaultCoreData();

        log.info(NEW_LINE +
                        "INITIALIZING CORE DATA (pattern settings={}, , pattern type={}, computation={}, computation settings={})",
                patternSettingsAutoconfig,
                computablePatternType,
                computationType,
                computationSettings);

        log.debug(NEW_LINE +
                        "Initialization parameters:" + NEW_LINE +
                        "Data folder name: {}" + NEW_LINE +
                        "Data file names: {}" + NEW_LINE +
                        "Run analysis at startup: {}" + NEW_LINE +
                        "Generate trading data: {}" + NEW_LINE +
                        "Create graphs for missing timeframes: {}" + NEW_LINE +
                        "Load trading data at startup: {}" + NEW_LINE +
                        "Override saved trading data: {}" + NEW_LINE +
                        "Purge computation data after trading data is generated: {}" + NEW_LINE +
                        "Pattern setting autoconfig: {}" + NEW_LINE +
                        "Computation settings autoconfig: {}" + NEW_LINE +
                        "Computation type: {}" + NEW_LINE +
                        "Computable pattern type: {}" + NEW_LINE +
                        "Atomic graph partition: {}" + NEW_LINE +
                        "Full scope prediction range: {}" + NEW_LINE +
                        "Launching automation after analysis: {}",
                dataFolderName,
                dataFilesNames,
                runAnalysisAtStartup,
                generateTradingData,
                createGraphsForMissingTimeframes,
                loadCoreDataAtStartup,
                overrideSavedCoreData,
                purgeAfterTradingDataGeneration,
                patternSettingsAutoconfig,
                computationSettings,
                computationType,
                computablePatternType,
                atomicPartition,
                fullScope,
                launchAutomation);

        //LOADING TRADING DATA IF APPLICABLE
        log.info("Loaded core data: {}", loadCoreDataAtStartup && loadCoreData(coreData));
        //RUNNING ANALYSIS IF APPLICABLE
        if (runAnalysisAtStartup) {
            log.info("Performed data analysis: {}", runAnalysis(
                    coreData,
                    dataFolderName,
                    dataFilesNames,
                    patternSettingsAutoconfig,
                    computationSettings,
                    computationType,
                    computablePatternType,
                    createGraphsForMissingTimeframes,
                    atomicPartition,
                    fullScope));

            if (generateTradingData && loadCoreDataAtStartup) {
                log.warn("Trading data loaded from file will be overriden by newly generated trading data.");
            }

            log.info("Saved core data overriden with newly generated core data: {}", overrideSavedCoreData && saveCoreData(coreData));

            //TODO: refactor and extract the field to a shared constant and DEBUG (the file is not aways created properly)
            log.info("Saved test core data overriden with newly generated core data: {}",
                    overrideSavedTestCoreData && saveCoreDataWithName(coreData, "datatest.ser"));


            boolean result = false;

            if (createTimestampedCoreDataArchive) {
                result = saveCoreDataWithName(coreData,
                        CORE_DATA_ARCHIVES_FOLDER_PATH +
                                "_" +
                                coreData.getPatternSettings().get(CommonCoreDataSettingNames.COMPUTATION_DATE)
                );
            }
            log.info("Created time stamped archive with newly generated data: {}", result);
        }
        //GENERATING TRADING DATA
        if (generateTradingData) {
            log.info("Generated trading data: {}", generateTradingData(coreData));

            log.info("Purged non-trading data: {}",
                    purgeNonTradingData(coreData, purgeAfterTradingDataGeneration));
        } else if (purgeAfterTradingDataGeneration != PurgeOption.NO) {
            log.warn("Non-trading data will not be purged as no trading data has been generated.");
        }
        //PRINTING LAUNCHING AUTOMATION IF APPLICABLE
        if (launchAutomation) {
            launchService.launchAutomation(coreData, this, patternComputingService, tradingService);
        }

        return coreData;
    }

    private boolean runAnalysis(CoreData coreData,
                                String dataFolderName,
                                List<String> dataFilesNames,
                                PatternSettings.Autoconfig patternSettingsAutoconfig,
                                ComputationSettings.Autoconfig computationSettings,
                                ComputationType computationType,
                                PatternSettings.PatternType computablePatternType,
                                boolean createGraphsForMissingTimeframes,
                                boolean atomicPartition,
                                boolean fullScope) {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        //LOADING GRAPHS
        log.debug("Loading graphs from folder {} with files {}...", dataFolderName, dataFilesNames);
        if (loadGraphs(coreData, dataFolderName, dataFilesNames)) {
            log.info("Created {} graph(s)", coreData.getGraphs().size());
        } else {
            log.error("Application could not initialize its data: no files of correct format could be read.");
        }
        //CREATING GRAPHS FOR MISSING TIMEFRAMES
        log.debug("Creating graphs for missing timeframes...");
        log.info("Created graphs for missing timeframes: {}",
                createGraphsForMissingTimeframes && createGraphsForMissingTimeframes(coreData));


        //CREATING PREDICTIVE PATTERNS
        log.info("Creating pattern boxes...");
        PatternSettings.Builder patternSettingsInput = new PatternSettings.Builder()
                .patternType(computablePatternType)
                .autoconfig(patternSettingsAutoconfig);
        if (fullScope) {
            patternSettingsInput = patternSettingsInput.scope("FULL");
        }

        if (atomicPartition) {
            patternSettingsInput = patternSettingsInput.atomizePartition();
        }

        if (patternComputingService.createPatternBoxes(coreData, patternSettingsInput)) {
            log.info("Created {} pattern box(es)", coreData.getPatternBoxes().size());
        } else {
            log.error("Application could not initialize its data: no pattern boxes could be created.");
        }
        //COMPUTING PREDICTIVE PATTERNS
        log.debug("Computing pattern boxes...");
        ComputationSettings.Builder computationSettingsInput = new ComputationSettings.Builder()
                .computationType(computationType)
                .autoconfig(computationSettings);

        if (patternComputingService.computePatternBoxes(coreData, computationSettingsInput)) {
            log.info("Computed {} pattern box(es)", coreData.getPatternBoxes().size());
        } else {
            log.error("Application could not initialize its data: no pattern boxes format could be computed.");
        }

        stopWatch.stop();

        log.info("Analysis time: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getLastTaskTimeMillis()));

        return Check.notNullNotEmpty(coreData.getPatternBoxes());
    }
}


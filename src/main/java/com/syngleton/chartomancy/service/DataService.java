package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.data.CoreData;
import com.syngleton.chartomancy.factory.GraphFactory;
import com.syngleton.chartomancy.model.charting.*;
import com.syngleton.chartomancy.util.Check;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
public class DataService {

    @Value("${reading_attempts}")
    private int readingAttempts;
    private final GraphFactory graphFactory;
    private static final String NEW_LINE = System.getProperty("line.separator");


    @Autowired
    public DataService(GraphFactory graphFactory) {
        this.graphFactory = graphFactory;

    }

    public boolean loadGraphs(CoreData coreData, String dataFolderName, List<String> dataFilesNames) {

        Set<Graph> graphs = new HashSet<>();

        if (coreData != null) {
            for (String dataFileName : dataFilesNames) {

                Graph graph = loadGraph("./" + dataFolderName + "/" + dataFileName);

                if (graph != null && !Check.matchesAnyChartObjectIn(graph, coreData.getGraphs())) {
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
            log.debug("*** CREATED GRAPH (name: {}, symbol: {}, timeframe: {}) ***",
                    graph.getName(),
                    graph.getSymbol(),
                    graph.getTimeframe());
            return graph;
        } else {
            log.error("File format header not found (parsed the first {} lines without success). List of supported headers:", readingAttempts);

            for (CSVFormat csvReader : CSVFormat.values()) {
                log.info("Format: {}, header: \"{}\"", csvReader, csvReader.formatHeader);
            }
            return null;
        }
    }

    //TODO Implement this method
    public boolean loadTradingData(CoreData coreData) {


        log.debug("NOT IMPLEMENTED YET");


        return false;
    }

    //TODO Implement this method
    public boolean saveTradingData(CoreData coreData) {
        log.debug("NOT IMPLEMENTED YET");
        return false;
    }

    //TODO Implement this method
    public boolean generateTradingData(CoreData coreData) {

        Set<PatternBox> tradingData = new HashSet<>();

        if ((coreData != null) && Check.notNullNotEmpty(coreData.getPatternBoxes())) {
            for (PatternBox patternBox : coreData.getPatternBoxes()) {
                List<Pattern> tradingPatterns = new ArrayList<>();

                if (Check.notNullNotEmpty(patternBox.getPatterns())) {
                    for (Pattern pattern : patternBox.getPatterns()) {
                        tradingPatterns.add(new TradingPattern((PredictivePattern) pattern));
                    }
                }
                if (!tradingPatterns.isEmpty()) {
                    tradingData.add(new PatternBox(tradingPatterns.get(0).getSymbol(), tradingPatterns.get(0).getTimeframe(), tradingPatterns));
                }
            }
            coreData.setTradingPatternBoxes(tradingData);
        }
        return !tradingData.isEmpty();
    }

    public boolean purgeNonTradingData(CoreData coreData) {
        return coreData.purge();
    }

    public boolean createGraphsForMissingTimeframes(CoreData coreData) {
        log.debug("NOT IMPLEMENTED YET");
        return false;
    }

    public void printCoreData(CoreData coreData) {

        StringBuilder coreDataToPrint = new StringBuilder();

        if (coreData != null)   {
            if (Check.notNullNotEmpty(coreData.getGraphs())) {
                coreDataToPrint.append(coreData.getGraphs().size())
                        .append(" GRAPH(S)")
                        .append(NEW_LINE);
                for (Graph graph : coreData.getGraphs())    {
                    coreDataToPrint
                            .append(graph.getName())
                            .append(", ")
                            .append(graph.getSymbol())
                            .append(", ")
                            .append(graph.getTimeframe())
                            .append(NEW_LINE)
                            .append("---")
                            .append(NEW_LINE);

                }
            } else {
                coreDataToPrint.append(coreData.getGraphs().size())
                        .append(NEW_LINE)
                        .append("0 GRAPH(S)")
                        .append(NEW_LINE);
            }

            if (Check.notNullNotEmpty(coreData.getPatternBoxes())) {
                coreDataToPrint.append(coreData.getPatternBoxes().size())
                        .append(" PATTERN BOX(ES)")
                        .append(NEW_LINE);
                for (PatternBox patternBox : coreData.getPatternBoxes())    {
                    coreDataToPrint
                            .append(patternBox.getPatterns().size())
                            .append(" patterns, ")
                            .append(patternBox.getSymbol())
                            .append(", ")
                            .append(patternBox.getTimeframe())
                            .append(NEW_LINE)
                            .append("---")
                            .append(NEW_LINE);
                }
            } else {
                coreDataToPrint.append(coreData.getGraphs().size())
                        .append("0 PATTERN BOX(ES)")
                        .append(NEW_LINE);
            }

            if (Check.notNullNotEmpty(coreData.getTradingPatternBoxes())) {
                coreDataToPrint.append(coreData.getTradingPatternBoxes().size())
                        .append(" TRADING PATTERN BOX(ES)")
                        .append(NEW_LINE);
                for (PatternBox tradingPatternBox : coreData.getTradingPatternBoxes())    {
                    coreDataToPrint
                            .append(tradingPatternBox.getPatterns().size())
                            .append(" trading patterns, ")
                            .append(tradingPatternBox.getSymbol())
                            .append(", ")
                            .append(tradingPatternBox.getTimeframe())
                            .append(NEW_LINE)
                            .append("---")
                            .append(NEW_LINE);
                }
            } else {
                coreDataToPrint.append(coreData.getGraphs().size())
                        .append("0 TRADING PATTERN BOX(ES)");
            }
        } else {
            coreDataToPrint.append("Core Data is empty.");
        }

        log.info(coreDataToPrint.toString());
    }

    public boolean printGraph(Graph graph) {

        if (graph != null) {
            log.info("*** PRINTING GRAPH (name: {}, symbol: {}, timeframe: {}) ***",
                    graph.getName(),
                    graph.getSymbol(),
                    graph.getCandles());
            int i = 1;
            for (Candle candle : graph.getCandles()) {
                log.info("{} -> {}", i++, candle.toString());
            }
            return true;
        } else {
            log.info("Cannot print graph: no data have been loaded.");
            return false;
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
}


package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.model.charting.Candle;
import com.syngleton.chartomancy.model.charting.Graph;
import com.syngleton.chartomancy.model.charting.Symbol;
import com.syngleton.chartomancy.model.charting.Timeframe;
import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static java.lang.Math.abs;

@Log4j2
@Service
public class DataService {

    @Value("${reading_attempts}")
    private int readingAttempts;


    public Set<Graph> loadGraphs(String dataFolderName, List<String> dataFilesNames) {

        Set<Graph> graphs = new HashSet<>();

        for (String dataFileName : dataFilesNames) {

            Graph graph = loadGraph("./" + dataFolderName + "/" + dataFileName);
            if (graph != null) {
                graphs.add(loadGraph("./" + dataFolderName + "/" + dataFileName));
            }
        }
        if (graphs.isEmpty()) {
            log.error("Application could not initialize its data: no files of correct format could be read.");
        } else {
            log.info("Created graph(s) from files with number: {}", graphs.size());
        }
        return graphs;
    }


    public Graph loadGraph(String path) {

        Graph graph;

        log.info("Reading file: " + path + "...");

        CSVFormat currentFormat = readFileFormat(path);

        if (currentFormat != null) {
            graph = createGraph(path, currentFormat);
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

    private Graph createGraph(String path, CSVFormat csvFormat) {
        String line;
        List<Candle> candles = new ArrayList<>();
        Symbol symbol = Symbol.UNDEFINED;
        Path filePath = Paths.get(path);

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            do {
                line = reader.readLine();
            } while (!line.matches(csvFormat.formatHeader));

            do {
                line = reader.readLine();
                if (line != null) {
                    String[] values = line.split(csvFormat.delimiter);
                    Candle candle = new Candle(
                            LocalDateTime.ofEpochSecond(Long.parseLong(
                                            Format.cutString(values[csvFormat.unixPosition], 10)),
                                    0,
                                    ZoneOffset.UTC),
                            Format.roundFloat(Float.parseFloat(values[csvFormat.openPosition])),
                            Format.roundFloat(Float.parseFloat(values[csvFormat.highPosition])),
                            Format.roundFloat(Float.parseFloat(values[csvFormat.lowPosition])),
                            Format.roundFloat(Float.parseFloat(values[csvFormat.closePosition])),
                            Format.roundFloat(Float.parseFloat(values[csvFormat.volumePosition]))
                    );
                    if (symbol == Symbol.UNDEFINED) {
                        symbol = readSymbol(values[csvFormat.symbolPosition]);
                    }

                    candles.add(candle);
                }
            } while (line != null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        candles.sort(Comparator.comparing(Candle::dateTime));
        return new Graph(filePath.getFileName().toString(), symbol, getTimeframe(candles), candles);
    }

    private Symbol readSymbol(String symbolValue) {

        Symbol symbol = Symbol.UNDEFINED;

        if ((symbolValue != null)
                && (symbolValue.contains("USD") || symbolValue.contains("usd"))) {
            if (symbolValue.contains("BTC") || symbolValue.contains("btc")) {
                symbol = Symbol.BTC_USD;
            } else if (symbolValue.contains("ETH") || symbolValue.contains("eth")) {
                symbol = Symbol.ETH_USD;
            }
        }

        return symbol;
    }

    private Timeframe getTimeframe(List<Candle> candles) {
        Timeframe timeframe = Timeframe.UNKNOWN;

        if (candles.size() > 1) {
            long timeBetweenCandles = abs(Duration.between(candles.get(0).dateTime(), candles.get(1).dateTime()).getSeconds());
            for (Timeframe tf : Timeframe.values()) {
                if (timeBetweenCandles == tf.durationInSeconds) {
                    timeframe = tf;
                }
            }
        }
        return timeframe;
    }
}


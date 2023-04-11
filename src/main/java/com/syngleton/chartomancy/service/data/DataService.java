package com.syngleton.chartomancy.service.data;

import com.syngleton.chartomancy.model.data.Candle;
import com.syngleton.chartomancy.model.data.Graph;
import com.syngleton.chartomancy.model.data.Timeframe;
import com.syngleton.chartomancy.util.Format;
import lombok.Getter;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.abs;

@Log4j2
@Service
public class DataService {

    @Getter
    private Graph graph = null;
    @Value("${reading_attempts}")
    private int readingAttempts;

    public boolean load(String path) {

        log.info("Reading file: " + path + "...");

        CSVFormats currentFormat = readFileFormat(path);

        if (currentFormat != null) {
            graph = createGraph(path, currentFormat);
            log.info("*** CREATED GRAPH (name: {}, symbol: {}, timeframe: {}) ***", graph.name(), graph.symbol(), graph.timeframe());
            return true;
        } else {
            log.error("File format header not found (parsed the first {} lines without success). List of supported headers:", readingAttempts);

            for (CSVFormats csvReader : CSVFormats.values()) {
                log.info("Format: {}, header: \"{}\"", csvReader, csvReader.formatHeader);
            }
            return false;
        }
    }

    public boolean printGraph() {

        if (graph != null) {
            log.info("*** PRINTING GRAPH (name: {}, symbol: {}, timeframe: {}) ***", graph.name(), graph.symbol(), graph.timeframe());
            int i = 1;
            for (Candle candle : graph.candles()) {
                log.info("{} -> {}", i++, candle.toString());
            }
            return true;
        } else {
            log.info("Cannot print graph: no data have been loaded.");
            return false;
        }
    }

    private CSVFormats readFileFormat(String path)  {

        CSVFormats csvFormat = null;
        String line;
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            do {
                line = reader.readLine();
                if (line != null) {
                    count++;
                    for (CSVFormats format : CSVFormats.values()) {

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

    private Graph createGraph(String path, CSVFormats csvFormat) {
        String line;
        List<Candle> candles = new ArrayList<>();
        String symbol = null;
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
                    if (symbol == null) {
                        symbol = values[csvFormat.symbolPosition];
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

    private Timeframe getTimeframe(List<Candle> candles)    {
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


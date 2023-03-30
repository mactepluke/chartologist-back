package com.syngleton.chartomancy.service;

import com.syngleton.chartomancy.model.Candle;
import com.syngleton.chartomancy.model.Graph;
import com.syngleton.chartomancy.util.CSVReaders;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


@Log4j2
@Service
public class DataService {

    private Graph graph;
    private static final int FORMAT_HEADER_READING_ATTEMPTS = 3;

    public void load(String path) {

        log.info("Reading file: " + path + "...");
        String line;
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            do {
                line = reader.readLine();
                if (line != null) {
                    count++;
                    for (CSVReaders csvReader : CSVReaders.values()) {
                        log.debug("Line -> {}", line);
                        log.debug("csvReader format -> {}", csvReader.getFormat());
                        log.debug("Assert -> {}", line.equals(csvReader.getFormat()));
                        if (line.equals(csvReader.getFormat())) {
                            graph = csvReader.createGraph(path);
                        }
                    }
                }
            } while (line != null && count < FORMAT_HEADER_READING_ATTEMPTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (count == FORMAT_HEADER_READING_ATTEMPTS) {
            log.error("File format header not found (parsed the first {} lines without success). List of supported headers:", FORMAT_HEADER_READING_ATTEMPTS);
            for (CSVReaders csvReader : CSVReaders.values()) {
                log.info(csvReader.getFormat());
            }
        }
    }

    public void printGraph() {
        log.info("*** PRINTING GRAPH ***");
        int i = 1;
        for (Candle candle : graph.candles())   {
            log.info("{} --> {}", i++,candle.toString());
        }
    }
}


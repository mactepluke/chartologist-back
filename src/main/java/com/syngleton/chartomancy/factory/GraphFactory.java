package com.syngleton.chartomancy.factory;

import com.syngleton.chartomancy.model.charting.Candle;
import com.syngleton.chartomancy.model.charting.Graph;
import com.syngleton.chartomancy.model.charting.Symbol;
import com.syngleton.chartomancy.model.charting.Timeframe;
import com.syngleton.chartomancy.service.CSVFormat;
import com.syngleton.chartomancy.util.Format;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

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
@Component
public class GraphFactory {

    public Graph create(String path, CSVFormat csvFormat) {
        String line;
        List<Candle> candles = new ArrayList<>();
        Symbol symbol = Symbol.UNDEFINED;
        Path filePath = Paths.get(path);

        try (
                BufferedReader reader = new BufferedReader(new FileReader(path))) {

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
        } catch (
                IOException e) {
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

    public Graph convertToUpperTimeframe(Graph graph, Timeframe timeframe)  {
//TODO Implement this method



        return graph;
    }
}

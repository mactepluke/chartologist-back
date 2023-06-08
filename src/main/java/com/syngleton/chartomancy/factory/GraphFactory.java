package com.syngleton.chartomancy.factory;

import com.syngleton.chartomancy.model.charting.candles.FloatCandle;
import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.service.misc.CSVFormat;
import com.syngleton.chartomancy.util.Check;
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

import static java.lang.Math.*;

@Log4j2
@Component
public class GraphFactory {

    public Graph create(String path, CSVFormat csvFormat) {
        String line;
        List<FloatCandle> floatCandles = new ArrayList<>();
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
                    FloatCandle floatCandle = new FloatCandle(
                            LocalDateTime.ofEpochSecond(Long.parseLong(
                                            Format.cutString(values[csvFormat.unixPosition], 10)),
                                    0,
                                    ZoneOffset.UTC),
                            Format.roundAccordingly(Float.parseFloat(values[csvFormat.openPosition])),
                            Format.roundAccordingly(Float.parseFloat(values[csvFormat.highPosition])),
                            Format.roundAccordingly(Float.parseFloat(values[csvFormat.lowPosition])),
                            Format.roundAccordingly(Float.parseFloat(values[csvFormat.closePosition])),
                            Format.roundAccordingly(Float.parseFloat(values[csvFormat.volumePosition]))
                    );
                    if (symbol == Symbol.UNDEFINED) {
                        symbol = readSymbol(values[csvFormat.symbolPosition]);
                    }

                    floatCandles.add(floatCandle);
                }
            } while (line != null);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        floatCandles.sort(Comparator.comparing(FloatCandle::dateTime));
        return new Graph(filePath.getFileName().toString(), symbol, getTimeframe(floatCandles), floatCandles);
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

    private Timeframe getTimeframe(List<FloatCandle> floatCandles) {
        Timeframe timeframe = Timeframe.UNKNOWN;

        if (floatCandles.size() > 1) {
            long timeBetweenCandles = abs(Duration.between(floatCandles.get(0).dateTime(), floatCandles.get(1).dateTime()).getSeconds());
            for (Timeframe tf : Timeframe.values()) {
                if (timeBetweenCandles == tf.durationInSeconds) {
                    timeframe = tf;
                }
            }
        }
        return timeframe;
    }

    public Graph upscaleTimeframe(Graph graph, Timeframe timeframe) {

        Graph upscaleGraph = null;

        if (graph != null
                && timeframe != null
                && timeframe != Timeframe.UNKNOWN
                && Check.notNullNotEmpty(graph.getFloatCandles())
                && timeframe.durationInSeconds > graph.getTimeframe().durationInSeconds) {

            List<FloatCandle> newFloatCandles = new ArrayList<>();

            int span = (int) (timeframe.durationInSeconds / graph.getTimeframe().durationInSeconds);

            for (int i = 0; i < graph.getFloatCandles().size() - span + 1; i = i + span) {
                LocalDateTime dateTime = graph.getFloatCandles().get(i).dateTime();
                float open = graph.getFloatCandles().get(i).open();
                float close = graph.getFloatCandles().get(i + span - 1).close();
                float high = open;
                float low = open;
                float volume = 0;

                for (int j = 0; j < span; j++) {
                    volume = volume + graph.getFloatCandles().get(i + j).volume();
                    high = max(high, graph.getFloatCandles().get(i + j).high());
                    low = min(low, graph.getFloatCandles().get(i + j).low());
                }
                newFloatCandles.add(new FloatCandle(dateTime, open, high, low, close, volume));
            }
            upscaleGraph = new Graph("Upscale-" + graph.getName(), graph.getSymbol(), timeframe, newFloatCandles);
        }
        return upscaleGraph;
    }
}

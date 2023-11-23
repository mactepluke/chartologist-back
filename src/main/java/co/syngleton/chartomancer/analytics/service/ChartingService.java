package co.syngleton.chartomancer.analytics.service;

import co.syngleton.chartomancer.analytics.model.FloatCandle;
import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.IntCandle;
import co.syngleton.chartomancer.analytics.model.Symbol;
import co.syngleton.chartomancer.analytics.model.Timeframe;
import co.syngleton.chartomancer.global.tools.Check;
import co.syngleton.chartomancer.global.tools.Format;
import co.syngleton.chartomancer.global.tools.Pair;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
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

import static co.syngleton.chartomancer.global.tools.Format.streamline;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

@Log4j2
@Service
@AllArgsConstructor
public class ChartingService implements CandleConverter {

    private static final int READING_ATTEMPTS = 3;

    public Graph generateGraphFromFile(String path) {

        Graph graph;

        log.info("Reading file... : " + path);

        CSVFormat currentFormat = readFileFormat(path);

        if (currentFormat != null) {
            graph = create(path, currentFormat);
            log.debug("*** CREATED GRAPH (name: {}, symbol: {}, timeframe: {}) ***", graph.getName(), graph.getSymbol(), graph.getTimeframe());
            return graph;
        } else {
            log.error("File format header not found (parsed the first {} lines without success). List of supported headers:", READING_ATTEMPTS);

            for (CSVFormat csvReader : CSVFormat.values()) {
                log.info("Format: {}, header: \"{}\"", csvReader, csvReader.formatHeader);
            }
            return null;
        }
    }

    public Graph generateGraphForTimeFrame(Graph graph, Timeframe timeframe) {
        return upscaleTimeframe(graph, timeframe);
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
            } while (line != null && count < READING_ATTEMPTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFormat;
    }

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
                if (line != null && !line.equals("")) {
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

        floatCandles = repairMissingCandles(floatCandles);

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

    private @NonNull List<FloatCandle> repairMissingCandles(@NonNull List<FloatCandle> floatCandles) {

        log.debug("Repairing missing candles...");

        List<FloatCandle> repairedFloatCandles = new ArrayList<>();
        Timeframe timeframe = getTimeframe(floatCandles);

        for (var i = 0; i < floatCandles.size() - 1; i++) {

            var j = 0;

            repairedFloatCandles.add(floatCandles.get(i));

            while ((i + j + 1 < floatCandles.size())
                    &&
                    (floatCandles.get(i + j + 1).dateTime().toEpochSecond(ZoneOffset.UTC) >
                            (floatCandles.get(i + j).dateTime().toEpochSecond(ZoneOffset.UTC) + timeframe.durationInSeconds))
            ) {

                FloatCandle missingCandle = new FloatCandle(
                        LocalDateTime.ofEpochSecond(floatCandles.get(i).dateTime().toEpochSecond(
                                ZoneOffset.UTC) + (j + 1) * timeframe.durationInSeconds, 0, ZoneOffset.UTC),
                        floatCandles.get(i).close(),
                        floatCandles.get(i).high(),
                        floatCandles.get(i).low(),
                        floatCandles.get(i).close(),
                        floatCandles.get(i).volume()
                );
                repairedFloatCandles.add(missingCandle);

                j++;
            }
        }
        repairedFloatCandles.add(floatCandles.get(floatCandles.size() - 1));

        return repairedFloatCandles;
    }

    private Timeframe getTimeframe(@NonNull List<FloatCandle> floatCandles) {
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

    public void printGraph(Graph graph) {

        if (graph != null) {
            log.info("*** PRINTING GRAPH (name: {}, symbol: {}, timeframe: {}) ***", graph.getName(), graph.getSymbol(), graph.getTimeframe());
            int i = 1;
            for (FloatCandle floatCandle : graph.getFloatCandles()) {
                log.info("{} -> {}", i++, floatCandle.toString());
            }
        } else {
            log.info("Cannot print graph: no data have been loaded.");
        }
    }

    @Override
    public List<IntCandle> rescaleToIntCandles(List<FloatCandle> floatCandles, int granularity) {

        Pair<Float, Float> extremes = getLowestAndHighest(floatCandles);

        List<IntCandle> intCandles = new ArrayList<>();

        for (FloatCandle floatCandle : floatCandles) {
            intCandles.add(rescaleToIntCandle(floatCandle, granularity, extremes.first(), extremes.second()));
        }
        return intCandles;
    }

    private Pair<Float, Float> getLowestAndHighest(List<FloatCandle> floatCandles) {

        float lowest = floatCandles.get(0).low();
        float highest = 0;

        for (FloatCandle floatCandle : floatCandles) {
            lowest = Math.min(lowest, floatCandle.low());
            highest = Math.max(highest, floatCandle.high());
        }
        return new Pair<>(lowest, highest);
    }

    private IntCandle rescaleToIntCandle(FloatCandle floatCandle, int granularity, float lowest, float highest) {

        int open = rescaleValue(floatCandle.open(), granularity, lowest, highest);
        int high = rescaleValue(floatCandle.high(), granularity, lowest, highest);
        int low = rescaleValue(floatCandle.low(), granularity, lowest, highest);
        int close = rescaleValue(floatCandle.close(), granularity, lowest, highest);
        int volume = round(floatCandle.volume() / ((highest - lowest) / granularity));

        return new IntCandle(LocalDateTime.now(), open, high, low, close, volume);
    }

    private int rescaleValue(float value, int granularity, float lowest, float highest) {

        float divider = (highest - lowest) / granularity;
        return streamline(round((value - lowest) / divider), 0, granularity);
    }

    public enum CSVFormat {
        CRYPTO_DATA_DOWNLOAD(
                "unix,date,symbol,open,high,low,close,Volume ...,Volume ...",
                ",",
                0,
                2,
                3,
                4,
                5,
                6,
                7
        );

        public final String formatHeader;
        public final String delimiter;
        public final int unixPosition;
        public final int symbolPosition;
        public final int openPosition;
        public final int highPosition;
        public final int lowPosition;
        public final int closePosition;
        public final int volumePosition;


        CSVFormat(String formatHeader,
                  String delimiter,
                  int unixPosition,
                  int symbolPosition,
                  int openPosition,
                  int highPosition,
                  int lowPosition,
                  int closePosition,
                  int volumePosition
        ) {
            this.formatHeader = formatHeader;
            this.delimiter = delimiter;
            this.unixPosition = unixPosition;
            this.symbolPosition = symbolPosition;
            this.openPosition = openPosition;
            this.highPosition = highPosition;
            this.lowPosition = lowPosition;
            this.closePosition = closePosition;
            this.volumePosition = volumePosition;
        }

    }

}

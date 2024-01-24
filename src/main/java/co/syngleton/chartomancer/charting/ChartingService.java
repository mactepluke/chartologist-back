package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.exception.InvalidFileFormatException;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.shared_domain.FloatCandle;
import co.syngleton.chartomancer.shared_domain.Graph;
import co.syngleton.chartomancer.shared_domain.IntCandle;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Format;
import co.syngleton.chartomancer.util.Pair;
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

import static co.syngleton.chartomancer.util.Format.streamline;
import static java.lang.Math.*;

@Log4j2
@Service
@AllArgsConstructor
final class ChartingService implements GraphGenerator, CandleRescaler {
    private static final int READING_ATTEMPTS = 3;
    private static final String NEW_LINE = System.lineSeparator();

    @Override
    public Graph generateGraphFromFile(String path) {

        log.debug("Reading file... : " + path);
        CSVFormat format = readFileFormat(path);

        if (format == null) {
            throw new InvalidFileFormatException("Unrecognized header format (parsed the first " + READING_ATTEMPTS + " lines). List of supported headers:" + NEW_LINE + listSupportedCSVHeaders());
        }

        Graph graph = generateGraphFromFileOfFormat(path, format);
        log.debug("*** CREATED GRAPH (name: {}, symbol: {}, timeframe: {}) ***", graph.getName(), graph.getSymbol(), graph.getTimeframe());
        return graph;
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
                    csvFormat = parseFileHeader(line);
                }
            } while (line != null && count < READING_ATTEMPTS && csvFormat == null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFormat;
    }

    private CSVFormat parseFileHeader(String header) {
        for (CSVFormat format : CSVFormat.values()) {
            log.debug(header + " vs " + format.formatHeader);
            if (header.equals(format.formatHeader)) {
                return format;
            }
        }
        return null;
    }

    private String listSupportedCSVHeaders() {
        StringBuilder sb = new StringBuilder();
        for (CSVFormat csvReader : CSVFormat.values()) {
            sb.append("Format: ").append(csvReader).append(", header: \"").append(csvReader.formatHeader).append("\"\n");
        }
        return sb.toString();
    }

    private Graph generateGraphFromFileOfFormat(String path, CSVFormat csvFormat) {
        List<FloatCandle> floatCandles;

        floatCandles = readFloatCandlesFromFile(path, csvFormat);
        floatCandles.sort(Comparator.comparing(FloatCandle::dateTime));
        floatCandles = repairMissingCandles(floatCandles);

        Path filePath = Paths.get(path);

        return new Graph(filePath.getFileName().toString(), csvFormat.symbol, getTimeframe(floatCandles), floatCandles);
    }

    private List<FloatCandle> readFloatCandlesFromFile(String path, CSVFormat csvFormat) {
        String line;
        List<FloatCandle> floatCandles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            boolean headerRead = false;

            while ((line = reader.readLine()) != null) {

                if (line.equals(csvFormat.formatHeader)) {
                    headerRead = true;
                    line = reader.readLine();
                }

                if (!line.isEmpty() && headerRead) {
                    String[] values = line.split(csvFormat.delimiter);
                    floatCandles.add(createFloatCandleFromValues(values, csvFormat));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return floatCandles;
    }

    private FloatCandle createFloatCandleFromValues(String[] values, CSVFormat csvFormat) {
        return new FloatCandle(
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

    @Override
    public Graph upscaleToTimeFrame(Graph graph, Timeframe timeframe) {

        if (!parametersAreValid(graph, timeframe)) {
            throw new InvalidParametersException("Upscaling parameters are invalid: graph: " + graph + ", timeframe: " + timeframe);
        }

        List<FloatCandle> newFloatCandles = new ArrayList<>();
        int span = (int) (timeframe.durationInSeconds / graph.getTimeframe().durationInSeconds);

        for (int i = 0; i < graph.getFloatCandles().size() - span + 1; i = i + span) {
            newFloatCandles.add(createUpscaleCandles(graph, span, i));
        }
        return new Graph("Upscale-" + graph.getName(), graph.getSymbol(), timeframe, newFloatCandles);
    }

    private boolean parametersAreValid(Graph graph, Timeframe timeframe) {
        return graph != null
                && timeframe != null
                && timeframe != Timeframe.UNKNOWN
                && Check.isNotEmpty(graph.getFloatCandles())
                && timeframe.durationInSeconds > graph.getTimeframe().durationInSeconds;
    }

    private FloatCandle createUpscaleCandles(Graph graph, int span, int step) {
        LocalDateTime dateTime = graph.getFloatCandles().get(step).dateTime();
        float open = graph.getFloatCandles().get(step).open();
        float close = graph.getFloatCandles().get(step + span - 1).close();
        float high = open;
        float low = open;
        float volume = 0;

        for (int i = 0; i < span; i++) {
            volume = volume + graph.getFloatCandles().get(step + i).volume();
            high = max(high, graph.getFloatCandles().get(step + i).high());
            low = min(low, graph.getFloatCandles().get(step + i).low());
        }
        return new FloatCandle(dateTime, open, high, low, close, volume);
    }

    @Override
    public List<IntCandle> rescale(List<FloatCandle> floatCandles, int granularity) {

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

    private enum CSVFormat {
        CRYPTO_DATA_DOWNLOAD_USD_BTC("unix,date,symbol,open,high,low,close,Volume USD,Volume BTC", ",", 0, 2, 3, 4, 5, 6, 7, Symbol.BTC_USD),
        CRYPTO_DATA_DOWNLOAD_USD_ETH("unix,date,symbol,open,high,low,close,Volume USD,Volume ETH", ",", 0, 2, 3, 4, 5, 6, 7, Symbol.ETH_USD),
        CRYPTO_DATA_DOWNLOAD_BTC_USD("unix,date,symbol,open,high,low,close,Volume BTC,Volume USD", ",", 0, 2, 3, 4, 5, 6, 8, Symbol.BTC_USD),
        CRYPTO_DATA_DOWNLOAD_ETH_USD("unix,date,symbol,open,high,low,close,Volume ETH,Volume USD", ",", 0, 2, 3, 4, 5, 6, 8, Symbol.ETH_USD);

        public final String formatHeader;
        public final String delimiter;
        public final int unixPosition;
        public final int symbolPosition;
        public final int openPosition;
        public final int highPosition;
        public final int lowPosition;
        public final int closePosition;
        public final int volumePosition;
        public final Symbol symbol;

        CSVFormat(String formatHeader,
                  String delimiter,
                  int unixPosition,
                  int symbolPosition,
                  int openPosition,
                  int highPosition,
                  int lowPosition,
                  int closePosition,
                  int volumePosition,
                  Symbol symbol
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
            this.symbol = symbol;
        }

    }


}

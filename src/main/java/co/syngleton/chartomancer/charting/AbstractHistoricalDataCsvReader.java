package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.Graph;
import co.syngleton.chartomancer.exception.InvalidFileFormatException;
import co.syngleton.chartomancer.util.Format;
import lombok.NonNull;

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
import java.util.Map;

import static java.lang.Math.abs;

abstract class AbstractHistoricalDataCsvReader implements HistoricalDataDAO {
    protected static final int READING_ATTEMPTS = 3;
    protected static final String NEW_LINE = System.lineSeparator();

    /**
     * This method must be overriden by the implementing class to initialize the formatHeaders map.
     * The key of the map is the full header of the CSV file, and the value is a ColumnProperties object
     * that must be initialized with by assigning each of its fields the position of the corresponding column in the CSV file.
     *
     * @return the map with all supported CSV formats headers and their corresponding ColumnProperties objects.
     */
    protected abstract Map<String, ColumnProperties> getFormatHeaders();

    @Override
    public final Graph generateGraphFromSource(String source) {
        return generateGraphFromFile(source);
    }

    private Graph generateGraphFromFile(String path) {

        String format = readFileFormat(path);

        if (format == null) {
            throw new InvalidFileFormatException("Unrecognized header format (parsed the first " + READING_ATTEMPTS + " lines). List of supported headers:" + NEW_LINE + listSupportedCSVHeaders());
        }

        return generateGraphFromFileOfFormat(path, format);
    }

    private String readFileFormat(String path) {
        String csvFormat = null;
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

    private String parseFileHeader(String header) {
        for (String format : getFormatHeaders().keySet()) {
            if (header.equals(format)) {
                return format;
            }
        }
        return null;
    }

    private String listSupportedCSVHeaders() {
        StringBuilder sb = new StringBuilder();
        for (String format : getFormatHeaders().keySet()) {
            sb.append("Format: ").append(format).append("\"\n");
        }
        return sb.toString();
    }

    private Graph generateGraphFromFileOfFormat(String path, String format) {
        List<FloatCandle> floatCandles;

        floatCandles = readFloatCandlesFromFile(path, format);
        floatCandles.sort(Comparator.comparing(FloatCandle::dateTime));

        Path filePath = Paths.get(path);

        return new Graph(filePath.getFileName().toString(), getFormatHeaders().get(format).symbol, getTimeframe(floatCandles), floatCandles);
    }

    private List<FloatCandle> readFloatCandlesFromFile(String path, String format) {
        String line;
        List<FloatCandle> floatCandles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            boolean headerRead = false;

            while ((line = reader.readLine()) != null) {

                if (line.equals(format)) {
                    headerRead = true;
                    line = reader.readLine();
                }

                if (!line.isEmpty() && headerRead) {
                    String[] values = line.split(getFormatHeaders().get(format).delimiter);
                    floatCandles.add(createFloatCandleFromValues(values, format));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return floatCandles;
    }

    private FloatCandle createFloatCandleFromValues(String[] values, String format) {
        return new FloatCandle(
                LocalDateTime.ofEpochSecond(Long.parseLong(
                                Format.cutString(values[getFormatHeaders().get(format).unixPosition], 10)),
                        0,
                        ZoneOffset.UTC),
                Format.roundAccordingly(Float.parseFloat(values[getFormatHeaders().get(format).openPosition])),
                Format.roundAccordingly(Float.parseFloat(values[getFormatHeaders().get(format).highPosition])),
                Format.roundAccordingly(Float.parseFloat(values[getFormatHeaders().get(format).lowPosition])),
                Format.roundAccordingly(Float.parseFloat(values[getFormatHeaders().get(format).closePosition])),
                Format.roundAccordingly(Float.parseFloat(values[getFormatHeaders().get(format).volumePosition]))
        );
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


    protected static final class ColumnProperties {
        private static final String DEFAULT_DELIMITER = ",";
        private final String delimiter;
        private final int unixPosition;
        private final int symbolPosition;
        private final int openPosition;
        private final int highPosition;
        private final int lowPosition;
        private final int closePosition;
        private final int volumePosition;
        private final Symbol symbol;

        ColumnProperties(int unixPosition,
                         int symbolPosition,
                         int openPosition,
                         int highPosition,
                         int lowPosition,
                         int closePosition,
                         int volumePosition,
                         Symbol symbol) {
            this(DEFAULT_DELIMITER,
                    unixPosition,
                    symbolPosition,
                    openPosition,
                    highPosition,
                    lowPosition,
                    closePosition,
                    volumePosition,
                    symbol
            );
        }

        ColumnProperties(String delimiter,
                         int unixPosition,
                         int symbolPosition,
                         int openPosition,
                         int highPosition,
                         int lowPosition,
                         int closePosition,
                         int volumePosition,
                         Symbol symbol) {
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

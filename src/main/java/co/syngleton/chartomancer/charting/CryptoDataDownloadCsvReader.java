package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.exception.InvalidFileFormatException;
import co.syngleton.chartomancer.shared_domain.FloatCandle;
import co.syngleton.chartomancer.shared_domain.Graph;
import co.syngleton.chartomancer.util.Format;
import lombok.NonNull;
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

@Component
@Log4j2
final class CryptoDataDownloadCsvReader extends HistoricalDataCsvReader {

    private static final int READING_ATTEMPTS = 3;
    private static final String NEW_LINE = System.lineSeparator();

    @Override
    public Graph generateGraphFromSource(String source) {
        return generateGraphFromFile(source);
    }

    private Graph generateGraphFromFile(String path) {

        CSVFormat format = readFileFormat(path);

        if (format == null) {
            throw new InvalidFileFormatException("Unrecognized header format (parsed the first " + READING_ATTEMPTS + " lines). List of supported headers:" + NEW_LINE + listSupportedCSVHeaders());
        }

        return generateGraphFromFileOfFormat(path, format);
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

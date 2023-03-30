package com.syngleton.chartomancy.util;

import com.syngleton.chartomancy.model.Candle;
import com.syngleton.chartomancy.model.Graph;
import com.syngleton.chartomancy.model.Timeframe;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public enum CSVReaders {
    CRYPTO_DATA_DOWNLOAD("unix,date,symbol,open,high,low,close,Volume USD,Volume BTC", ",");

    private final String format;
    private final String delimiter;

    CSVReaders(String format, String delimiter) {
        this.format = format;
        this.delimiter = delimiter;
    }

    public String getFormat() {
        return format;
    }

    public Graph createGraph(String path) {

        String line;
        List<Candle> candles = new ArrayList<>();
        String symbol = null;
        Path filePath = Paths.get(path);

        switch (this) {
            case CRYPTO_DATA_DOWNLOAD:
                try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

                    do {
                        line = reader.readLine();
                    } while (!line.equals(this.getFormat()));

                    do {
                        line = reader.readLine();
                        if (line != null) {
                            String[] values = line.split(this.delimiter);
                            Candle candle = new Candle(
                                    //TODO Fix the incorrect reading of too long unix strings
                                    LocalDateTime.ofEpochSecond(Long.parseLong(values[0]), 0, ZoneOffset.UTC),
                                    Format.roundFloat(Float.parseFloat(values[3])),
                                    Format.roundFloat(Float.parseFloat(values[4])),
                                    Format.roundFloat(Float.parseFloat(values[5])),
                                    Format.roundFloat(Float.parseFloat(values[6])),
                                    Format.roundFloat(Float.parseFloat(values[7]))
                            );
                            if (symbol == null) {
                                symbol = values[2];
                            }
                            candles.add(candle);
                        }
                    } while (line != null);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return new Graph(filePath.getFileName().toString(), symbol, Timeframe.DAY, candles);
    }
}

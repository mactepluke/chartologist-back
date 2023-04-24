package com.syngleton.chartomancy;

import com.syngleton.chartomancy.model.charting.Candle;
import com.syngleton.chartomancy.model.charting.Graph;
import com.syngleton.chartomancy.model.charting.Symbol;
import com.syngleton.chartomancy.model.charting.Timeframe;
import com.syngleton.chartomancy.util.Format;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Getter
public class MockData {

    private final static int TEST_GRAPH_LENGTH = 3000;
    private final static int TEST_GRAPH_STARTING_DATETIME = 1666051200;
    private final static Timeframe TEST_TIMEFRAME = Timeframe.DAY;
    private final static Symbol TEST_SYMBOL = Symbol.UNDEFINED;
    private final static int TEST_STARTING_OPEN = 2000;
    private final static int MINIMUM_VALUE = 400;
    private final static int MAXIMUM_VALUE = 5000;
    private final static float VARIABILITY_COEF = 300;
    private final static int NUMBER_OF_DIFFERENT_MOCK_TIMEFRAMES = 2;

    private final List<Candle> mockCandles = new ArrayList<>();
    private final Graph mockGraphDay1;
    private final Graph mockGraphDay2;
    private final Graph mockGraphHour;
    private final Set<Graph> testGraphs;
    private final int testGraphLength;
    private final int numberOfDifferentMockTimeframes;

    public MockData() {

        this.testGraphLength = TEST_GRAPH_LENGTH;
        this.numberOfDifferentMockTimeframes = NUMBER_OF_DIFFERENT_MOCK_TIMEFRAMES;

        for (int i = 0; i < testGraphLength; i++) {
            Random rd = new Random();
            float span = (float) Math.random() * VARIABILITY_COEF;
            boolean direction = rd.nextBoolean();
            float open = (i == 0) ? TEST_STARTING_OPEN : mockCandles.get(i - 1).close();
            float close = (direction) ? open + span : open - span;
            float high = (float) ((direction) ? close + Math.random() * VARIABILITY_COEF / 2 : open + Math.random() * VARIABILITY_COEF / 2);
            float low = (float) ((direction) ? open - Math.random() * VARIABILITY_COEF / 2 : close - Math.random() * VARIABILITY_COEF / 2);
            float volume = (float) Math.random() * VARIABILITY_COEF * TEST_STARTING_OPEN;

            Candle candle = new Candle(
                    LocalDateTime.ofEpochSecond(
                            TEST_GRAPH_STARTING_DATETIME + TEST_TIMEFRAME.durationInSeconds * i,
                            0,
                            ZoneOffset.UTC),
                    Format.streamlineFloat(open, MINIMUM_VALUE, MAXIMUM_VALUE),
                    Format.streamlineFloat(high, MINIMUM_VALUE, MAXIMUM_VALUE),
                    Format.streamlineFloat(low, MINIMUM_VALUE, MAXIMUM_VALUE),
                    Format.streamlineFloat(close, MINIMUM_VALUE, MAXIMUM_VALUE),
                    volume
            );
            mockCandles.add(candle);
        }

        mockGraphDay1 = new Graph("Mock graph day 1", TEST_SYMBOL, Timeframe.DAY, mockCandles);
        mockGraphDay2 = new Graph("Mock graph day 2", TEST_SYMBOL, Timeframe.DAY, mockCandles);
        mockGraphHour = new Graph("Mock graph hour", TEST_SYMBOL, Timeframe.HOUR, mockCandles);

        testGraphs = new HashSet<>(Arrays.asList(mockGraphDay1, mockGraphDay2, mockGraphHour));


    }
}

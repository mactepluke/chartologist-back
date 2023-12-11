package co.syngleton.chartomancer.configuration;

import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.Symbol;
import co.syngleton.chartomancer.analytics.model.Timeframe;
import co.syngleton.chartomancer.analytics.model.FloatCandle;
import co.syngleton.chartomancer.global.tools.Format;
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

    private final List<FloatCandle> mockFloatCandles = new ArrayList<>();
    private final int testGraphLength;
    private final int numberOfDifferentMockTimeframes;
    private Graph mockGraphDay1;
    private  Graph mockGraphDay2;
    private  Graph mockGraphHour;
    private  Set<Graph> testGraphs;

    public MockData() {

        this.testGraphLength = TEST_GRAPH_LENGTH;
        this.numberOfDifferentMockTimeframes = NUMBER_OF_DIFFERENT_MOCK_TIMEFRAMES;

        for (int i = 0; i < testGraphLength; i++) {
            Random rd = new Random();
            float span = (float) Math.random() * VARIABILITY_COEF;
            boolean direction = rd.nextBoolean();
            float open = (i == 0) ? TEST_STARTING_OPEN : mockFloatCandles.get(i - 1).close();
            float close = (direction) ? open + span : open - span;
            float high = (float) ((direction) ? close + Math.random() * VARIABILITY_COEF / 2 : open + Math.random() * VARIABILITY_COEF / 2);
            float low = (float) ((direction) ? open - Math.random() * VARIABILITY_COEF / 2 : close - Math.random() * VARIABILITY_COEF / 2);
            float volume = (float) Math.random() * VARIABILITY_COEF * TEST_STARTING_OPEN;

            FloatCandle floatCandle = new FloatCandle(
                    LocalDateTime.ofEpochSecond(
                            TEST_GRAPH_STARTING_DATETIME + TEST_TIMEFRAME.durationInSeconds * i,
                            0,
                            ZoneOffset.UTC),
                    Format.streamline(open, MINIMUM_VALUE, MAXIMUM_VALUE),
                    Format.streamline(high, MINIMUM_VALUE, MAXIMUM_VALUE),
                    Format.streamline(low, MINIMUM_VALUE, MAXIMUM_VALUE),
                    Format.streamline(close, MINIMUM_VALUE, MAXIMUM_VALUE),
                    volume
            );
            mockFloatCandles.add(floatCandle);
        }
        resetGraphs();
    }

    public void resetGraphs() {

        mockGraphDay1 = new Graph("Mock graph day 1", TEST_SYMBOL, Timeframe.DAY, mockFloatCandles);
        mockGraphDay2 = new Graph("Mock graph day 2", TEST_SYMBOL, Timeframe.DAY, mockFloatCandles);
        mockGraphHour = new Graph("Mock graph hour", TEST_SYMBOL, Timeframe.HOUR, mockFloatCandles);

        testGraphs = new HashSet<>(List.of(mockGraphDay1, mockGraphDay2, mockGraphHour));
    }
}

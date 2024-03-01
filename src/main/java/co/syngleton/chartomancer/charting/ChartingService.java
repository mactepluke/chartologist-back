package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.Graph;
import co.syngleton.chartomancer.core_entities.IntCandle;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.util.Check;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static co.syngleton.chartomancer.util.Format.streamline;
import static java.lang.Math.*;

@Log4j2
@Service
@AllArgsConstructor
final class ChartingService implements GraphGenerator, CandleRescaler, GraphSlicer {
    private final HistoricalDataDAO historicalDataDAO;
    private final ChartingProperties chartingProperties;

    @Override
    public Graph generateGraphFromHistoricalDataSource(String source) {

        log.debug("Generating graphs from source... : " + source);

        Graph graph = historicalDataDAO.generateGraphFromSource(source);

        if (graphIsInvalid(graph)) {
            return null;
        }

        if (chartingProperties.repairMissingCandles()) {
            graph = repairMissingCandles(graph);
        }

        log.debug("*** CREATED GRAPH (name: {}, symbol: {}, timeframe: {}) ***", graph.getName(), graph.getSymbol(), graph.getTimeframe());

        return graph;
    }

    private boolean graphIsInvalid(Graph graph) {

        if (graph == null || Check.isEmpty(graph.getFloatCandles())) {
            log.error("Graph is null or empty.");
            return true;
        }

        if (graph.getTimeframe() == Timeframe.UNKNOWN || graph.getTimeframe() == null) {
            log.warn("Graph has unknown or null timeframe.");
        }

        if (graph.getSymbol() == Symbol.UNDEFINED || graph.getSymbol() == null) {
            log.warn("Graph has undefined or null symbol.");
        }

        return false;
    }

    Graph repairMissingCandles(Graph graph) {

        if (graphIsInvalid(graph)) {
            return null;
        }

        log.debug("Repairing missing candles...");

        List<FloatCandle> repairedFloatCandles = new ArrayList<>();

        for (var i = 0; i < graph.getFloatCandles().size() - 1; i++) {

            var j = 0;

            repairedFloatCandles.add(graph.getFloatCandles().get(i));

            while ((i + j + 1 < graph.getFloatCandles().size())
                    &&
                    (graph.getFloatCandles().get(i + j + 1).dateTime().toEpochSecond(ZoneOffset.UTC) >
                            (graph.getFloatCandles().get(i + j).dateTime().toEpochSecond(ZoneOffset.UTC) + graph.getTimeframe().durationInSeconds))
            ) {

                LocalDateTime newCandleDateTime = LocalDateTime.ofEpochSecond(graph.getFloatCandles().get(i).dateTime().toEpochSecond(
                        ZoneOffset.UTC) + (j + 1) * graph.getTimeframe().durationInSeconds, 0, ZoneOffset.UTC);

                float newCandleOpen = graph.getFloatCandles().get(i).close();
                float newCandleClose = graph.getFloatCandles().get(i + j + 1).open();
                float newCandleHigh = max(newCandleOpen, newCandleClose);
                float newCandleLow = min(newCandleOpen, newCandleClose);
                float newCandleVolume = (graph.getFloatCandles().get(i).volume() + graph.getFloatCandles().get(i + j + 1).volume()) / 2;

                FloatCandle missingCandle = new FloatCandle(newCandleDateTime, newCandleOpen, newCandleHigh, newCandleLow, newCandleClose, newCandleVolume);
                repairedFloatCandles.add(missingCandle);

                j++;
            }
        }
        repairedFloatCandles.add(graph.getFloatCandles().get(graph.getFloatCandles().size() - 1));

        return new Graph(graph.getName(), graph.getSymbol(), graph.getTimeframe(), repairedFloatCandles);
    }

    @Override
    public Graph upscaleToTimeFrame(Graph graph, Timeframe timeframe) {

        if (!parametersAreValid(graph, timeframe)) {
            throw new InvalidParametersException("Upscaling parameters are invalid: graph: " + graph + ", timeframe: " + timeframe);
        }

        List<FloatCandle> newFloatCandles = new ArrayList<>();
        int span = (int) (timeframe.durationInSeconds / graph.getTimeframe().durationInSeconds);

        for (int i = 0; i < graph.getFloatCandles().size() - span + 1; i = i + span) {
            FloatCandle newFloatCandle = createUpscaleCandle(graph, span, i);
            if (newFloatCandle != null) {
                newFloatCandles.add(createUpscaleCandle(graph, span, i));
            }
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

    FloatCandle createUpscaleCandle(Graph graph, int span, int step) {

        if (graphIsInvalid(graph)) {
            return null;
        }

        if (graph.getFloatCandles().size() < step + span) {
            return null;
        }

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

        List<Float> extremes = getLowestAndHighestCandleValuesAndVolumes(floatCandles);

        List<IntCandle> intCandles = new ArrayList<>();

        for (FloatCandle floatCandle : floatCandles) {
            intCandles.add(
                    rescaleToIntCandle(floatCandle,
                            granularity,
                            extremes.get(0),
                            extremes.get(1),
                            extremes.get(2),
                            extremes.get(3))
            );
        }
        return intCandles;
    }

    List<Float> getLowestAndHighestCandleValuesAndVolumes(List<FloatCandle> floatCandles) {

        float lowest = floatCandles.get(0).low();
        float highest = floatCandles.get(0).high();
        float lowestVolume = floatCandles.get(0).volume();
        float highestVolume = floatCandles.get(0).volume();

        for (FloatCandle floatCandle : floatCandles) {
            lowest = Math.min(lowest, floatCandle.low());
            highest = Math.max(highest, floatCandle.high());
            lowestVolume = Math.min(lowestVolume, floatCandle.volume());
            highestVolume = Math.max(highestVolume, floatCandle.volume());
        }
        return List.of(lowest, highest, lowestVolume, highestVolume);
    }

    IntCandle rescaleToIntCandle(FloatCandle floatCandle,
                                 int granularity,
                                 float lowest,
                                 float highest,
                                 float lowestVolume,
                                 float highestVolume) {

        int open = rescaleValue(floatCandle.open(), granularity, lowest, highest);
        int high = rescaleValue(floatCandle.high(), granularity, lowest, highest);
        int low = rescaleValue(floatCandle.low(), granularity, lowest, highest);
        int close = rescaleValue(floatCandle.close(), granularity, lowest, highest);
        int volume = rescaleValue(floatCandle.volume(), granularity, lowestVolume, highestVolume);

        return new IntCandle(LocalDateTime.now(), open, high, low, close, volume);
    }

    private int rescaleValue(final float value, final int granularity, final float lowest, final float highest) {

        float divider = highest == lowest ? 1 : (highest - lowest) / granularity;
        return streamline(round((value - lowest) / divider), 0, granularity);
    }

    @Override
    public Graph getSlice(final Graph graph, final LocalDateTime startDate, final LocalDateTime endDate) {

        if (graphIsInvalid(graph) || startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new InvalidParametersException("Graph is invalid: " + graph);
        }

        final long startCandle;
        final long endCandle;

        if (graph.getFloatCandles().get(graph.getFloatCandles().size() - 1).dateTime().isBefore(startDate) ||
                graph.getFloatCandles().get(0).dateTime().isAfter(endDate)) {
            return new Graph(
                    "EmptySlice-" + graph.getName(),
                    graph.getSymbol(),
                    graph.getTimeframe(),
                    new ArrayList<>()
            );
        }

        if (graph.getFloatCandles().get(0).dateTime().isAfter(startDate)) {
            startCandle = 0;
        } else {
            startCandle = getNumberOfCandlesBetweenDates(graph, graph.getFloatCandles().get(0).dateTime(), startDate);
        }
        if (graph.getFloatCandles().get(graph.getFloatCandles().size() - 1).dateTime().isBefore(endDate)) {
            endCandle = graph.getFloatCandles().size();
        } else {
            endCandle = getNumberOfCandlesBetweenDates(graph, graph.getFloatCandles().get(0).dateTime(), endDate);
        }
        return new Graph(
                "Slice-" + graph.getName(),
                graph.getSymbol(),
                graph.getTimeframe(),
                graph.getFloatCandles().subList((int) startCandle, (int) endCandle)
        );
    }

    private long getNumberOfCandlesBetweenDates(Graph graph, LocalDateTime startDate, LocalDateTime endDate) {
        return abs(Duration.between(startDate, endDate).toSeconds() / graph.getTimeframe().durationInSeconds);
    }

    @Override
    public Graph getSlice(Graph graph, LocalDate startDate, LocalDate endDate) {
        return getSlice(graph, startDate.atStartOfDay(), endDate.atStartOfDay());
    }


}

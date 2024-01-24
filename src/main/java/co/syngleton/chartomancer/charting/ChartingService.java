package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.shared_domain.FloatCandle;
import co.syngleton.chartomancer.shared_domain.Graph;
import co.syngleton.chartomancer.shared_domain.IntCandle;
import co.syngleton.chartomancer.util.Check;
import co.syngleton.chartomancer.util.Pair;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static co.syngleton.chartomancer.util.Format.streamline;
import static java.lang.Math.*;

@Log4j2
@Service
@AllArgsConstructor
final class ChartingService implements GraphGenerator, CandleRescaler {
    private final HistoricalDataDAO historicalDataDAO;
    private final ChartingProperties chartingProperties;

    //TODO Make this method more universal
    @Override
    public Graph generateGraphFromFile(String path) {

        log.debug("Generating graphs from file... : " + path);

        Graph graph = historicalDataDAO.generateGraphFromSource(path);

        if (chartingProperties.isRepairMissingCandles()) {
            graph = repairMissingCandles(graph);
        }

        log.debug("*** CREATED GRAPH (name: {}, symbol: {}, timeframe: {}) ***", graph.getName(), graph.getSymbol(), graph.getTimeframe());

        return graph;
    }

    private @NonNull Graph repairMissingCandles(@NonNull Graph graph) {

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

                FloatCandle missingCandle = new FloatCandle(
                        LocalDateTime.ofEpochSecond(graph.getFloatCandles().get(i).dateTime().toEpochSecond(
                                ZoneOffset.UTC) + (j + 1) * graph.getTimeframe().durationInSeconds, 0, ZoneOffset.UTC),
                        graph.getFloatCandles().get(i).close(),
                        graph.getFloatCandles().get(i).high(),
                        graph.getFloatCandles().get(i).low(),
                        graph.getFloatCandles().get(i).close(),
                        graph.getFloatCandles().get(i).volume()
                );
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


}

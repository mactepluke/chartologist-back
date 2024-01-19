package co.syngleton.chartomancer.external_api_requesting;

import co.syngleton.chartomancer.charting.GraphUpscaler;
import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.shared_domain.FloatCandle;
import co.syngleton.chartomancer.shared_domain.Graph;
import co.syngleton.chartomancer.util.Format;
import com.jsoniter.JsonIterator;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Log4j2
final class CryptoCompareRequestingService implements DataRequestingService {
    private static final String SOURCE_API_NAME = "CryptoCompare_";
    private static final int MAX_CANDLES_TO_FETCH = 250;
    private final CryptoCompareApiProxy cryptoCompareApiProxy;
    private final GraphUpscaler graphUpscaler;
    private final String apiKey;
    private final boolean freeSubscription;

    public CryptoCompareRequestingService(CryptoCompareApiProxy cryptoCompareApiProxy,
                                          GraphUpscaler graphUpscaler,
                                          String apiKey,
                                          boolean freeSubscription) {
        this.cryptoCompareApiProxy = cryptoCompareApiProxy;
        this.graphUpscaler = graphUpscaler;
        this.apiKey = apiKey;
        this.freeSubscription = freeSubscription;
    }

    private Symbol adjustSymbol(Symbol symbol) {
        if (freeSubscription && symbol != Symbol.BTC_USD) {
            log.warn("Free subscription: symbol is forced to BTC_USD by default.");
            symbol = Symbol.BTC_USD;
        }
        return symbol;
    }

    public Graph getLatestPriceHistoryGraphWithCurrentPriceCandle(Symbol symbol, Timeframe timeframe, int size) {

        Graph graph = getLatestPriceHistoryGraph(symbol, timeframe, size);
        float currentPrice = getCurrentPrice(symbol);

        FloatCandle floatCandle = new FloatCandle(
                LocalDateTime.now(),
                graph.getFloatCandles().get(graph.getFloatCandles().size() - 1).close(),
                0,
                0,
                currentPrice,
                0);

        List<FloatCandle> newCandles = new ArrayList<>(graph.getFloatCandles());
        newCandles.add(floatCandle);

        return new Graph(graph.getName(), graph.getSymbol(), graph.getTimeframe(), newCandles);
    }

    @Override
    public float getCurrentPrice(Symbol symbol) {

        symbol = adjustSymbol(symbol);

        String response = cryptoCompareApiProxy.getCurrentPrice(symbol.base, symbol.quote, apiKey);

        switch (symbol.quote) {
            case "EUR" -> {
                //TO IMPLEMENT IF NECESSARY
                return 0;
            }
            case "ETH" -> {
                //TO IMPLEMENT IF NECESSARY
                return -1;
            }
            default -> {
                PriceInUsdDto price = JsonIterator.deserialize(response, PriceInUsdDto.class);
                return Format.roundAccordingly(price.getUSD());
            }
        }
    }

    private Graph getLatestPriceHistoryGraph(Symbol symbol, Timeframe timeframe, int size) {

        Graph graph;

        symbol = adjustSymbol(symbol);

        checkInputParams(symbol, timeframe, size);

        //Size is adapted so the API return (size) * candles instead of (size + 1), as it looks to be an index of a table starting with 0
        size = size - 1;

        switch (timeframe) {
            case HOUR -> graph = getLatestHourPriceHistoryGraph(symbol, size);
            case FOUR_HOUR -> graph = getLatestFourHourPriceHistoryGraph(symbol, size);
            case DAY -> graph = getLatestDayPriceHistoryGraph(symbol, size);
            case WEEK -> graph = getLatestWeekPriceHistoryGraph(symbol, size);
            default -> throw new InvalidParametersException("Timeframe=" + timeframe + " is unsupported.");
        }
        return graph;
    }

    private void checkInputParams(Symbol symbol, Timeframe timeframe, int size) {
        if (symbol == null
                || symbol == Symbol.UNDEFINED
                || timeframe == null
                || timeframe == Timeframe.UNKNOWN
                || size < 1
                || size > MAX_CANDLES_TO_FETCH) {
            throw new InvalidParametersException("Symbol or timeframe are not defined, or size is out of bounds [1-2000]");
        }
    }

    private @NonNull Graph getLatestHourPriceHistoryGraph(@NonNull Symbol symbol, int size) {

        Graph graph;

        String response = cryptoCompareApiProxy.getHourGraph(symbol.base, symbol.quote, size, apiKey);
        CryptoCompareOhlcvDto dto = JsonIterator.deserialize(response, CryptoCompareOhlcvDto.class);

        graph = new Graph(SOURCE_API_NAME + Format.toFileNameCompatibleDateTime(LocalDateTime.now()),
                symbol,
                Timeframe.HOUR,
                convertOhlcvDtoToFloatCandles(dto));

        return graph;
    }

    private Graph getLatestFourHourPriceHistoryGraph(@NonNull Symbol symbol, int size) {

        Graph graph;

        String response = cryptoCompareApiProxy.getHourGraph(symbol.base, symbol.quote, size * 4, apiKey);
        CryptoCompareOhlcvDto dto = JsonIterator.deserialize(response, CryptoCompareOhlcvDto.class);

        graph = new Graph(SOURCE_API_NAME + Format.toFileNameCompatibleDateTime(LocalDateTime.now()),
                symbol,
                Timeframe.HOUR,
                convertOhlcvDtoToFloatCandles(dto));

        return graphUpscaler.upscaleToTimeFrame(graph, Timeframe.FOUR_HOUR);
    }

    private @NonNull Graph getLatestDayPriceHistoryGraph(@NonNull Symbol symbol, int size) {

        Graph graph;

        String response = cryptoCompareApiProxy.getDayGraph(symbol.base, symbol.quote, size, apiKey);
        CryptoCompareOhlcvDto dto = JsonIterator.deserialize(response, CryptoCompareOhlcvDto.class);

        graph = new Graph(SOURCE_API_NAME + Format.toFileNameCompatibleDateTime(LocalDateTime.now()),
                symbol,
                Timeframe.DAY,
                convertOhlcvDtoToFloatCandles(dto));

        return graph;
    }

    private Graph getLatestWeekPriceHistoryGraph(@NonNull Symbol symbol, int size) {

        Graph graph;

        String response = cryptoCompareApiProxy.getDayGraph(symbol.base, symbol.quote, size * 7, apiKey);
        CryptoCompareOhlcvDto dto = JsonIterator.deserialize(response, CryptoCompareOhlcvDto.class);

        graph = new Graph(SOURCE_API_NAME + Format.toFileNameCompatibleDateTime(LocalDateTime.now()),
                symbol,
                Timeframe.DAY,
                convertOhlcvDtoToFloatCandles(dto));

        return graphUpscaler.upscaleToTimeFrame(graph, Timeframe.WEEK);
    }

    private @NonNull List<FloatCandle> convertOhlcvDtoToFloatCandles(CryptoCompareOhlcvDto cryptoCompareOhlcvDto) {

        List<FloatCandle> floatCandles = new ArrayList<>();

        if (checkDtoIntegrity(cryptoCompareOhlcvDto)) {
            floatCandles = cryptoCompareOhlcvDto.getData().getData()
                    .stream()
                    .map(this::convertOhlcvDtoToFloatCandle)
                    .toList();
        }
        return floatCandles;
    }

    private FloatCandle convertOhlcvDtoToFloatCandle(CryptoCompareOhlcvCandle candle) {
        return new FloatCandle(
                LocalDateTime.ofEpochSecond(candle.getTime(),
                        0,
                        ZoneOffset.UTC),
                candle.getOpen(),
                candle.getHigh(),
                candle.getLow(),
                candle.getClose(),
                candle.getClose());
    }

    private boolean checkDtoIntegrity(CryptoCompareOhlcvDto cryptoCompareOhlcvDto) {
        return cryptoCompareOhlcvDto != null
                && cryptoCompareOhlcvDto.getType() == 100
                && cryptoCompareOhlcvDto.getResponse().equals("Success")
                && cryptoCompareOhlcvDto.getData() != null;
    }

}

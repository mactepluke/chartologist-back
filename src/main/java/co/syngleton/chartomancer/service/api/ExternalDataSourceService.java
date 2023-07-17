package co.syngleton.chartomancer.service.api;

import co.syngleton.chartomancer.model.charting.misc.Graph;
import co.syngleton.chartomancer.model.charting.misc.Symbol;
import co.syngleton.chartomancer.model.charting.misc.Timeframe;

public interface ExternalDataSourceService {

    float getCurrentPrice(Symbol symbol);

    Graph getLatestPriceHistoryGraph(Symbol symbol, Timeframe timeframe, int size);

    Graph getLatestPriceHistoryGraphWithCurrentPriceCandle(Symbol symbol, Timeframe timeframe, int size);
}

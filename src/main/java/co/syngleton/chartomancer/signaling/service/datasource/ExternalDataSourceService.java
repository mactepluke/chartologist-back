package co.syngleton.chartomancer.signaling.service.datasource;

import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.Symbol;
import co.syngleton.chartomancer.analytics.model.Timeframe;

public interface ExternalDataSourceService {

    float getCurrentPrice(Symbol symbol);

    Graph getLatestPriceHistoryGraph(Symbol symbol, Timeframe timeframe, int size);

    Graph getLatestPriceHistoryGraphWithCurrentPriceCandle(Symbol symbol, Timeframe timeframe, int size);
}

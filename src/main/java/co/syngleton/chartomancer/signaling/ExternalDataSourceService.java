package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.domain.Graph;
import co.syngleton.chartomancer.domain.Symbol;
import co.syngleton.chartomancer.domain.Timeframe;

public interface ExternalDataSourceService {

    float getCurrentPrice(Symbol symbol);

    Graph getLatestPriceHistoryGraph(Symbol symbol, Timeframe timeframe, int size);

    Graph getLatestPriceHistoryGraphWithCurrentPriceCandle(Symbol symbol, Timeframe timeframe, int size);
}

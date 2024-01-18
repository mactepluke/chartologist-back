package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.shared_domain.Graph;

public interface ExternalDataSourceService {

    float getCurrentPrice(Symbol symbol);

    Graph getLatestPriceHistoryGraph(Symbol symbol, Timeframe timeframe, int size);

    Graph getLatestPriceHistoryGraphWithCurrentPriceCandle(Symbol symbol, Timeframe timeframe, int size);
}

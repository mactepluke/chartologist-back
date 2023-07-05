package com.syngleton.chartomancy.service.api;

import com.syngleton.chartomancy.model.charting.misc.Graph;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;

public interface ExternalDataSourceService {

    float getCurrentPrice(Symbol symbol);

    Graph getLatestPriceHistoryGraph(Symbol symbol, Timeframe timeframe, int size);

    Graph getLatestPriceHistoryGraphWithCurrentPriceCandle(Symbol symbol, Timeframe timeframe, int size);
}

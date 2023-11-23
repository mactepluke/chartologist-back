package co.syngleton.chartomancer.analytics.service;

import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.Timeframe;

public interface GraphUpscaler {
    Graph upscaleTimeframe(Graph graph, Timeframe timeframe);
}

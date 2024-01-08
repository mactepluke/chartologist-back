package co.syngleton.chartomancer.contracts;

import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.Timeframe;

public interface GraphUpscalor {
    Graph upscaleToTimeFrame(Graph graph, Timeframe timeframe);
}

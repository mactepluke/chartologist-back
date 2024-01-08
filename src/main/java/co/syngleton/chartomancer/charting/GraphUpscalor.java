package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.domain.Graph;
import co.syngleton.chartomancer.domain.Timeframe;

public interface GraphUpscalor {
    Graph upscaleToTimeFrame(Graph graph, Timeframe timeframe);
}

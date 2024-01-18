package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.shared_domain.Graph;

public interface GraphUpscalor {
    Graph upscaleToTimeFrame(Graph graph, Timeframe timeframe);
}

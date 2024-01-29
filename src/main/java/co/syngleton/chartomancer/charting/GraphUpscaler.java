package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.Graph;

public interface GraphUpscaler {
    Graph upscaleToTimeFrame(Graph graph, Timeframe timeframe);
}

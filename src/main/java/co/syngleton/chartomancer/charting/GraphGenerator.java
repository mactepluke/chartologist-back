package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.core_entities.Graph;

public interface GraphGenerator extends GraphUpscaler {
    Graph generateGraphFromHistoricalDataSource(String path);
}

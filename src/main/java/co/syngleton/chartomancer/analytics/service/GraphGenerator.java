package co.syngleton.chartomancer.analytics.service;

import co.syngleton.chartomancer.analytics.model.Graph;

public interface GraphGenerator extends GraphUpscaler {
    Graph generateGraphFromFile(String path);
}

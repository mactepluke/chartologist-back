package co.syngleton.chartomancer.contracts;

import co.syngleton.chartomancer.analytics.model.Graph;

public interface GraphGenerator extends GraphUpscalor {
    Graph generateGraphFromFile(String path);
}

package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.shared_domain.Graph;

public interface GraphGenerator extends GraphUpscalor {
    Graph generateGraphFromFile(String path);
}

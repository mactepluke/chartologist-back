package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.shared_domain.Graph;

interface HistoricalDataDAO {

    Graph generateGraphFromSource(String source);
}

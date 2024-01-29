package co.syngleton.chartomancer.charting;

import co.syngleton.chartomancer.core_entities.Graph;

interface HistoricalDataDAO {

    Graph generateGraphFromSource(String source);
}

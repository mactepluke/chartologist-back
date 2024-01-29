package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.core_entities.DefaultCoreData;
import co.syngleton.chartomancer.core_entities.Graph;
import co.syngleton.chartomancer.core_entities.PatternBox;

import java.util.Map;
import java.util.Set;

public class TestableCoreData extends DefaultCoreData {

    public TestableCoreData() {
        super();
    }

    public void setGraphs(Set<Graph> graphs) {
        this.graphs = graphs;
    }

    public void setPatternBoxes(Set<PatternBox> patternBoxes) {
        this.patternBoxes = patternBoxes;
    }

    public void setPatternSettings(Map<String, String> patternSettings) {
        this.patternSettings = patternSettings;
    }

    public void setTradingPatternBoxes(Set<PatternBox> tradingPatternBoxes) {
        this.tradingPatternBoxes = tradingPatternBoxes;
    }

    public void setTradingPatternSettings(Map<String, String> tradingPatternSettings) {
        this.tradingPatternSettings = tradingPatternSettings;
    }


}

package co.syngleton.chartomancer.shared_domain;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CoreData extends Serializable {
    void copy(@NonNull CoreData coreData);

    void pushTradingPatternData(Set<PatternBox> patternBoxes);

    void purgeNonTrading();

    Graph getGraph(Symbol symbol, Timeframe timeframe);

    Optional<PatternBox> getPatternBox(Symbol symbol, Timeframe timeframe);

    Optional<PatternBox> getTradingPatternBox(Symbol symbol, Timeframe timeframe);

    Set<Graph> getGraphs();

    void setGraphs(Set<Graph> graphs);

    Set<PatternBox> getPatternBoxes();

    void setPatternBoxes(Set<PatternBox> patternBoxes);

    Map<String, String> getPatternSettings();

    void setPatternSettings(Map<String, String> patternSettings);

    void setPatternSetting(String key, String value);

    Set<PatternBox> getTradingPatternBoxes();

    void setTradingPatternBoxes(Set<PatternBox> tradingPatternBoxes);

    Map<String, String> getTradingPatternSettings();

    void setTradingPatternSettings(Map<String, String> tradingPatternSettings);

    void setTradingPatternSetting(String key, String value);
}

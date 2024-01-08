package co.syngleton.chartomancer.data;

import co.syngleton.chartomancer.domain.Graph;
import co.syngleton.chartomancer.domain.PatternBox;
import co.syngleton.chartomancer.domain.Symbol;
import co.syngleton.chartomancer.domain.Timeframe;
import lombok.NonNull;

import java.io.Serializable;
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

    DataSettings getPatternSettings();

    void setPatternSettings(DataSettings patternSettings);

    Set<PatternBox> getTradingPatternBoxes();

    void setTradingPatternBoxes(Set<PatternBox> tradingPatternBoxes);

    DataSettings getTradingPatternSettings();

    void setTradingPatternSettings(DataSettings tradingPatternSettings);
}

package co.syngleton.chartomancer.contracts;

import co.syngleton.chartomancer.analytics.model.Graph;
import co.syngleton.chartomancer.analytics.model.PatternBox;
import co.syngleton.chartomancer.analytics.model.Symbol;
import co.syngleton.chartomancer.analytics.model.Timeframe;
import co.syngleton.chartomancer.data.DataSettings;
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

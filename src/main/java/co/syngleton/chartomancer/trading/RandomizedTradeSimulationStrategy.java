package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.Graph;

import java.util.concurrent.ThreadLocalRandom;

final class RandomizedTradeSimulationStrategy extends TradeSimulationStrategy {

    private RandomizedTradeSimulationStrategy(Graph graph, CoreData coreData, TradingAccount account) {
        super(graph, coreData, account);
    }

    static TradeSimulationStrategy newInstanceOf(Graph graph, CoreData coreData, TradingAccount account) {
        return new RandomizedTradeSimulationStrategy(graph, coreData, account);
    }

    @Override
    void setNextOpenCandle() {

        setTradeOpenCandle(ThreadLocalRandom.current().nextInt(getBoundary() - 1));

        if (this.getTradeOpenCandle() <= this.getPatternLength()) {
            setTradeOpenCandle(this.getPatternLength() + 1);
        }
    }
}

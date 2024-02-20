package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.Graph;

import java.time.ZoneOffset;

import static java.lang.Math.round;

final class IterativeTradeSimulationStrategy extends TradeSimulationStrategy {

    private IterativeTradeSimulationStrategy(Graph graph, CoreData coreData, TradingAccount account) {
        super(graph, coreData, account);
    }

    static TradeSimulationStrategy newInstanceOf(Graph graph, CoreData coreData, TradingAccount account) {
        return new IterativeTradeSimulationStrategy(graph, coreData, account);
    }

    @Override
    void setNextOpenCandle() {

        if (hasBlankTrade()) {
            incrementTradeOpenCandle();
            return;
        }
        setTradeOpenCandle(getTradeOpenCandle() + (round((getTrade().getCloseDateTime().toEpochSecond(ZoneOffset.UTC)
                - getTrade().getOpenDateTime().toEpochSecond(ZoneOffset.UTC))
                / (float) getTrade().getTimeframe().durationInSeconds) + 1));
    }
}


package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.Graph;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;


@ToString
public abstract class TradeSimulationStrategy {
    @Getter
    private final Graph graph;
    @Getter
    private final CoreData coreData;
    @Getter
    private final TradingAccount account;
    @Getter
    private final int boundary;
    @Getter
    private final int patternLength;
    private int blankTradesCount;
    @Getter
    private int tradeOpenCandle;
    @Getter
    private Trade trade;

    protected TradeSimulationStrategy(Graph graph, CoreData coreData, TradingAccount account) {
        this.graph = graph;
        this.coreData = coreData;
        this.account = account;
        this.boundary = graph.getFloatCandles().size() - coreData.getMaxTradingScope(graph.getSymbol(), graph.getTimeframe());
        this.patternLength = coreData.getTradingPatternLength(graph.getSymbol(), graph.getTimeframe());
        this.blankTradesCount = 0;
        this.tradeOpenCandle = patternLength + 1;
        this.trade = Trade.blank();
    }

    public static TradeSimulationStrategy randomize(Graph graph, CoreData coreData, TradingAccount account) {
        return RandomizedTradeSimulationStrategy.newInstanceOf(graph, coreData, account);
    }

    public static TradeSimulationStrategy iterate(Graph graph, CoreData coreData, TradingAccount account) {
        return IterativeTradeSimulationStrategy.newInstanceOf(graph, coreData, account);
    }

    abstract void setNextOpenCandle();

    void incrementTradeOpenCandle() {
        tradeOpenCandle++;
    }

    boolean hasUnfundedTrade() {
        return trade.isUnfunded();
    }

    protected boolean hasBlankTrade() {
        return trade.isBlank();
    }

    int countBlankTrades() {
        return blankTradesCount;
    }

    protected void setTradeOpenCandle(int tradeOpenCandle) {
        this.tradeOpenCandle = tradeOpenCandle;
    }

    protected Trade getTrade() {
        return trade;
    }

    void setTrade(Trade trade) {
        Objects.requireNonNull(trade);

        if (hasBlankTrade()) {
            incrementBlankTradesCount();
        }
        this.trade = trade;
    }

    TradingSimulationResult exportResult() {
        return new TradingSimulationResult(this.account, countBlankTrades());
    }

    private void incrementBlankTradesCount() {
        blankTradesCount++;
    }

}

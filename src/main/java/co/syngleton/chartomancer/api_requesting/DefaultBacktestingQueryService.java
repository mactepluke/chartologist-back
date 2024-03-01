package co.syngleton.chartomancer.api_requesting;

import co.syngleton.chartomancer.charting.GraphSlicer;
import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.FloatCandle;
import co.syngleton.chartomancer.core_entities.Graph;
import co.syngleton.chartomancer.core_entities.Pattern;
import co.syngleton.chartomancer.trading.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
@AllArgsConstructor
class DefaultBacktestingQueryService implements BacktestingQueryService {
//TODO Core data must be initialized with test graph portion outside of computing data
    private final RequestingTradingService requestingTradingService;
    private final CoreData coreData;
    private final GraphSlicer graphSlicer;

    @Override
    public BacktestingResultsDTO getTradingSimulation(Symbol symbol, Timeframe timeframe, LocalDate startDate, LocalDate endDate, double accountBalance) {

        final List<Pattern> tradingPatterns = this.coreData.getTradingPatterns(symbol, timeframe);
        Objects.requireNonNull(tradingPatterns, "Trading patterns could not be retrieved from the core data.");

        if (tradingPatterns.isEmpty()) {
            log.warn("No trading patterns found for symbol {} and timeframe {}", symbol, timeframe);
            return null;
        }

        final Graph graph = coreData.getGraph(symbol, timeframe);

        if (graph == null) {
            log.warn("No graphs found for symbol {} and timeframe {}", symbol, timeframe);
            return null;
        }

        final Graph graphSlice = graphSlicer.getSlice(coreData.getGraph(symbol, timeframe), startDate, endDate);

        final TradingAccount tradingAccount = new TradingAccount();
        tradingAccount.credit(accountBalance);

        final TradingConditionsChecker checker = intializeTradingConditionsChecker();

        final TradeSimulationStrategy strategy = TradeSimulationStrategy.iterate(
                graphSlice,
                this.coreData,
                tradingAccount
        );

        final TradingSimulationResult tradingSimulationResult = requestingTradingService.simulateTrades(strategy, checker);

        return BacktestingResultsDTO.from(tradingSimulationResult);
    }

    private TradingConditionsChecker intializeTradingConditionsChecker() {
        return TradingConditionsChecker.builder()
                .maxTrades(1000)
                .maxBlankTrades(1000)
                .maximumAccountBalance(100000000)
                .minimumAccountBalance(1)
                .build();
    }
}

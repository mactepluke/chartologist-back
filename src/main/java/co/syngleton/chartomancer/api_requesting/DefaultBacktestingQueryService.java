package co.syngleton.chartomancer.api_requesting;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.Graph;
import co.syngleton.chartomancer.trading.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
@Log4j2
@AllArgsConstructor
class DefaultBacktestingQueryService implements BacktestingQueryService {

    private final RequestingTradingService requestingTradingService;
    private final CoreData coreData;

    //TODO Finish implementing this method properly after writing a more thorough test
    @Override
    public BacktestingResultsDTO getTradingSimulation(Symbol symbol, Timeframe timeframe, LocalDate startDate, LocalDate endDate, float accountBalance) {

        TradingAccount tradingAccount = new TradingAccount();
        tradingAccount.credit(accountBalance);

        TradingConditionsChecker checker = TradingConditionsChecker.builder()
                .maxTrades(100)
                .maxBlankTrades(1000)
                .maximumAccountBalance(10000)
                .minimumAccountBalance(5000)
                .build();

        TradeSimulationStrategy strategy = TradeSimulationStrategy.iterate(new Graph("Graph partition", symbol, timeframe, new ArrayList<>()), this.coreData, tradingAccount);

        TradingSimulationResult tradingSimulationResult = requestingTradingService.simulateTrades(strategy, checker);

        return BacktestingResultsDTO.from(tradingSimulationResult);
    }

}

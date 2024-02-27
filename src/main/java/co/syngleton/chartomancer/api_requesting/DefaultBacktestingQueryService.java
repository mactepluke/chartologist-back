package co.syngleton.chartomancer.api_requesting;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Log4j2
@AllArgsConstructor
class DefaultBacktestingQueryService implements BacktestingQueryService {

    @Override
    public BacktestingResultsDTO getTradingSimulation(Symbol symbol, Timeframe timeframe, LocalDate startDate, LocalDate endDate, float accountBalance) {
        return null;
    }

}

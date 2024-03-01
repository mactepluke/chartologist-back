package co.syngleton.chartomancer.api_requesting;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;

import java.time.LocalDate;

public interface BacktestingQueryService {

    BacktestingResultsDTO getTradingSimulation(Symbol symbol, Timeframe timeframe, LocalDate startDate, LocalDate endDate, double accountBalance);
}

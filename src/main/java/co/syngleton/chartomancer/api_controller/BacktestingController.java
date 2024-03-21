package co.syngleton.chartomancer.api_controller;

import co.syngleton.chartomancer.api_requesting.BacktestingQueryService;
import co.syngleton.chartomancer.api_requesting.BacktestingResultsDTO;
import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Log4j2
@RestController
@RequestMapping("/backtesting")
@Validated
@Scope("request")
@AllArgsConstructor
class BacktestingController {

    private final BacktestingQueryService queryService;

    //http://localhost:9240/backtesting/get-results?symbol=<symbol>&timeframe=<timeframe>&startDate=<startDate>&endDate=<endDate>&accountBalance=<accountBalance>
    @GetMapping("/get-results")
    ResponseEntity<BacktestingResultsDTO> getBacktestingResults(
            @RequestParam @NotNull Symbol symbol,
            @RequestParam @NotNull Timeframe timeframe,
            @RequestParam @NotBlank @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @NotBlank @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = true) @DecimalMin(value = "0.0", inclusive = false) @DecimalMax(value = "100000000") float accountBalance
    ) {

        log.info("Received request for backtesting results for symbol: " + symbol +
                ", timeframe: " + timeframe +
                ", start date: " + startDate +
                ", end date: " + endDate +
                ", account balance: " + accountBalance);

        if (startDate.isAfter(endDate)) {
            throw new NonComputableArgumentsException("Start date cannot be after end date.");
        }
        if (Symbol.UNDEFINED.equals(symbol)) {
            throw new NonComputableArgumentsException("Symbol is undefined.");
        }
        if (Timeframe.UNKNOWN.equals(timeframe)) {
            throw new NonComputableArgumentsException("Timeframe is unknown.");
        }

        BacktestingResultsDTO results = queryService.getTradingSimulation(symbol, timeframe, startDate, endDate, accountBalance);

        if (results == null) {
            throw new UnexpectedRequestResultException("No results were returned by the provider.");
        }
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

}

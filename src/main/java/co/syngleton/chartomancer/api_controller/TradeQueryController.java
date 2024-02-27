package co.syngleton.chartomancer.api_controller;

import co.syngleton.chartomancer.api_requesting.TradeQueryService;
import co.syngleton.chartomancer.api_requesting.TradeSignalDTO;
import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.trading.Trade;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Log4j2
@RestController
@RequestMapping("/trading")
@Scope("request")
@AllArgsConstructor
class TradeQueryController {

    private final TradeQueryService queryService;

    //http://localhost:9240/trading/getbesttrade?symbol=<symbol>
    @GetMapping("/getbesttrade")
    ResponseEntity<TradeSignalDTO> getBestTrade(
            @RequestParam Symbol symbol
    ) {
        HttpStatus status;
        TradeSignalDTO tradeSignalDTO = null;

        if (symbol == null
                || symbol == Symbol.UNDEFINED) {
            throw new InvalidParametersException("Undefined timeframe or symbol.");
        }

        Trade trade = queryService.getCurrentBestTrade(symbol);

        if (trade == null) {
            status = NO_CONTENT;
        } else {
            tradeSignalDTO = TradeSignalDTO.from(trade);
            status = OK;
        }
        return new ResponseEntity<>(tradeSignalDTO, status);
    }

    //http://localhost:9240/trading/getbesttradefortimeframe?symbol=<symbol>&timeframe=<timeframe>
    @GetMapping("/getbesttradefortimeframe")
    ResponseEntity<TradeSignalDTO> getBestTradeForTimeframe(
            @RequestParam Symbol symbol,
            @RequestParam Timeframe timeframe
    ) {
        HttpStatus status;
        TradeSignalDTO tradeSignalDTO = null;

        if (symbol == null
                || timeframe == null
                || timeframe == Timeframe.UNKNOWN
                || symbol == Symbol.UNDEFINED) {
            throw new InvalidParametersException("Undefined timeframe or symbol.");
        }

        Trade trade = queryService.getCurrentBestTrade(symbol, timeframe);

        if (trade == null) {
            status = NO_CONTENT;
        } else {
            tradeSignalDTO = TradeSignalDTO.from(trade);
            status = OK;
        }
        return new ResponseEntity<>(tradeSignalDTO, status);
    }

}

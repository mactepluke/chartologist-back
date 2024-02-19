package co.syngleton.chartomancer.api_controller;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.signaling.TradeSignalDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/dummytrades")
@Scope("request")
@AllArgsConstructor
public class DummyTradingController {

    //http://localhost:9240/dummytrades/getbasic?symbol=<symbol>&timeframe=<timeframe>&startDate=<startDate>&endDate=<endDate>&accountBalance=<accountBalance>
    @GetMapping("/getbasic")
    ResponseEntity<TradeSignalDTO> getBestTrade(
            @RequestParam Symbol symbol,
            @RequestParam Timeframe timeframe,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam float accountBalance
    ) {
        /*HttpStatus status;
        TradeSignalDTO tradeSignalDTO = null;

        if (symbol == null
                || symbol == Symbol.UNDEFINED) {
            throw new InvalidParametersException("Undefined timeframe or symbol.");
        }

        Trade trade = tradeQueryService.getCurrentBestTrade(symbol);

        if (trade == null) {
            status = NO_CONTENT;
        } else {
            tradeSignalDTO = new TradeSignalDTO(trade);
            status = OK;
        }
        return new ResponseEntity<>(tradeSignalDTO, status);*/
        return null;
    }

}

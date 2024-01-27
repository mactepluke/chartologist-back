package co.syngleton.chartomancer.controller;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.signaling.TradeSignalDTO;
import co.syngleton.chartomancer.trading.Trade;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TradingRequestController {

    private final TradingRequestManager tradingRequestManager;

    @Autowired
    public TradingRequestController(TradingRequestManager tradingRequestManager) {
        this.tradingRequestManager = tradingRequestManager;
    }

    //http://localhost:8080/trading/getbesttrade?symbol=<symbol>
    @GetMapping("/getbesttrade")
    public ResponseEntity<TradeSignalDTO> getBestTrade(
            @RequestParam Symbol symbol
    ) {
        HttpStatus status;
        TradeSignalDTO tradeSignalDTO = null;

        if (symbol == null
                || symbol == Symbol.UNDEFINED) {
            throw new InvalidParametersException("Undefined timeframe or symbol.");
        }

        Trade trade = tradingRequestManager.getCurrentBestTrade(symbol);

        if (trade == null) {
            status = NO_CONTENT;
        } else {
            tradeSignalDTO = new TradeSignalDTO(trade);
            status = OK;
        }
        return new ResponseEntity<>(tradeSignalDTO, status);
    }

    //http://localhost:8080/trading/getbesttradefortimeframe?symbol=<symbol>&timeframe=<timeframe>
    @GetMapping("/getbesttradefortimeframe")
    public ResponseEntity<TradeSignalDTO> getBestTradeForTimeframe(
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

        Trade trade = tradingRequestManager.getCurrentBestTrade(symbol, timeframe);

        if (trade == null) {
            status = NO_CONTENT;
        } else {
            tradeSignalDTO = new TradeSignalDTO(trade);
            status = OK;
        }
        return new ResponseEntity<>(tradeSignalDTO, status);
    }

    //http://localhost:8080/trading/subscribeToSignals?email=<email>
    @GetMapping("/subscribeToSignals")
    public ResponseEntity<String> subscribeToSignals(
            @RequestParam String email
    ) {
        HttpStatus status;
        String response;

        //TODO implement subscription
        status = OK;
        response = "Subscription confirmed with email: " + email;

        return new ResponseEntity<>(response, status);
    }


}

package com.syngleton.chartomancy.controller.trading;

import com.syngleton.chartomancy.dto.internal.TradeDTO;
import com.syngleton.chartomancy.exceptions.InvalidParametersException;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.model.trading.Trade;
import com.syngleton.chartomancy.service.enduser.TradingRequestManager;
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
    public ResponseEntity<TradeDTO> getBestTrade(
            @RequestParam Symbol symbol
    ) {
        HttpStatus status;
        TradeDTO tradeDTO = null;

        if (symbol == null
                || symbol == Symbol.UNDEFINED) {
            throw new InvalidParametersException("Undefined timeframe or symbol.");
        }

        Trade trade = tradingRequestManager.getCurrentBestTrade(symbol);

        if (trade == null) {
            status = NO_CONTENT;
        } else {
            tradeDTO = new TradeDTO(trade);
            status = OK;
        }
        return new ResponseEntity<>(tradeDTO, status);
    }

    //http://localhost:8080/trading/getbesttradefortimeframe?symbol=<symbol>&timeframe=<timeframe>
    @GetMapping("/getbesttradefortimeframe")
    public ResponseEntity<TradeDTO> getBestTradeForTimeframe(
            @RequestParam Symbol symbol,
            @RequestParam Timeframe timeframe
    ) {
        HttpStatus status;
        TradeDTO tradeDTO = null;

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
            tradeDTO = new TradeDTO(trade);
            status = OK;
        }
        return new ResponseEntity<>(tradeDTO, status);
    }

    //http://localhost:8080/trading/subscribeToSignals?email=<email>
    @GetMapping("/subscribeToSignals")
    public ResponseEntity<String> subscribeToSignals(
            @RequestParam String email
    ) {
        HttpStatus status;
        String response;

        tradingRequestManager.subscribeToSignals(email);
        status = OK;
        response = "Subscription confirmed with email: " + email;

        return new ResponseEntity<>(response, status);
    }


}

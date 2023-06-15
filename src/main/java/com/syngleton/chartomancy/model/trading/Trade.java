package com.syngleton.chartomancy.model.trading;

import com.syngleton.chartomancy.model.charting.misc.*;
import com.syngleton.chartomancy.model.trading.interfaces.Accountable;
import com.syngleton.chartomancy.util.Calc;
import com.syngleton.chartomancy.util.Format;
import com.syngleton.chartomancy.util.datatabletool.PrintableData;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.max;

@Log4j2
@Getter
public class Trade extends ChartObject implements PrintableData {

    private static final String OPEN_NAME = "Open";
    private static final String LAST_UPDATE_NAME = "Last update";
    private static final String PLATFORM_NAME = "Platform";
    private static final String SYMBOL_NAME = "Symbol";
    private static final String TIMEFRAME_NAME = "Timeframe";
    private static final String ACCOUNT_BALANCE_NAME = "Account balance at open";
    private static final String SIZE_NAME = "Size";
    private static final String EXPECTED_CLOSE_NAME = "Expected close";
    private static final String EXPIRY_NAME = "Expiry";
    private static final String CLOSE_NAME = "Close";
    private static final String STATUS_NAME = "Status";
    private static final String SIDE_NAME = "Side";
    private static final String OPENING_PRICE_NAME = "Opening price";
    private static final String CLOSING_PRICE_NAME = "Closing price";
    private static final String TP_NAME = "Take profit";
    private static final String SL_NAME = "Stop loss";
    private static final String LEVERAGE_NAME = "Leverage";
    private static final String TP_PRICE_PER_NAME = "TP price %";
    private static final String SL_PRICE_PER_NAME = "SL price %";
    private static final String EXPECTED_PROFIT_NAME = "Expected profit";
    private static final String RISK_PER_NAME = "Risk %";
    private static final String RR_RATIO_NAME = "R/R ratio";
    private static final String PNL_NAME = "PnL";

    private static final long MAX_TRADE_DURATION_IN_SECONDS = Timeframe.WEEK.durationInSeconds * 4;
    private static final int MAX_LEVERAGE = 20;

    private LocalDateTime lastUpdate;
    private final String platform;
    private final double accountBalanceAtOpen;
    private final double size;
    private final boolean side;
    private final double openingPrice;
    private final LocalDateTime openDateTime;
    private final LocalDateTime expectedClose;
    private LocalDateTime expiry;
    private LocalDateTime closeDateTime;
    private TradeStatus status;
    private double closingPrice;
    private double takeProfit;
    private double stopLoss;

    private Trade() {
        platform = "Blank trade";
        accountBalanceAtOpen = 0;
        size = 0;
        side = false;
        openingPrice = 0;
        openDateTime = null;
        expectedClose = null;
        status = TradeStatus.BLANK;
    }

    /**
     * Creates a fully parameterized trade
     * @param platform name of the exchange or the dummy graph to trade against
     * @param timeframe
     * @param symbol
     * @param account account from which to read the balance
     * @param openDateTime
     * @param size this is the token quantity (not the price of the tokens!)
     * @param expectedClose
     * @param side long if true, short if false
     * @param openingPrice
     * @param takeProfit
     * @param stopLoss
     */
    public Trade(String platform,
                 Timeframe timeframe,
                 Symbol symbol,
                 Accountable account,
                 LocalDateTime openDateTime,
                 double size,
                 LocalDateTime expectedClose,
                 boolean side,
                 double openingPrice,
                 double takeProfit,
                 double stopLoss) {

        super(symbol, timeframe);

        this.status = TradeStatus.OPENED;
        this.openingPrice = openingPrice;
        this.side = side;

        setStopLoss(stopLoss);
        setTakeProfit(takeProfit);

        this.platform = platform == null ? "unknown" : platform;
        this.accountBalanceAtOpen = Format.roundTwoDigits(account.getBalance());
        this.openDateTime = openDateTime == null ? LocalDateTime.now() : openDateTime;
        this.expectedClose = expectedClose == null ?
                LocalDateTime.now().plusSeconds(timeframe.durationInSeconds * timeframe.scope)
                : expectedClose;
        this.expiry = LocalDateTime.now().plusSeconds(MAX_TRADE_DURATION_IN_SECONDS);
        this.closeDateTime = null;

        this.size = Format.roundNDigits(size, 3);

        if (getMaxLoss() > accountBalanceAtOpen) {
            this.status = TradeStatus.UNFUNDED;
        }

        if (getStopLoss() == getTakeProfit() || getTakeProfit() == openingPrice || getStopLoss() == openingPrice)   {
            this.status = TradeStatus.BLANK;
        }

        this.lastUpdate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Trade{" +
                OPEN_NAME + "=" + Format.toFrenchDateTime(openDateTime) + ", " +
                LAST_UPDATE_NAME + "=" + Format.toFrenchDateTime(lastUpdate) + ", " +
                PLATFORM_NAME + "=" + getPlatform() + ", " +
                SYMBOL_NAME + "=" + getSymbol() + ", " +
                TIMEFRAME_NAME + "=" + getTimeframe() + ", " +
                ACCOUNT_BALANCE_NAME + "=" + accountBalanceAtOpen + ", " +
                SIZE_NAME + "=" + size + ", " +
                EXPECTED_CLOSE_NAME + "=" + Format.toFrenchDateTime(expectedClose) + ", " +
                EXPIRY_NAME + "=" + Format.toFrenchDateTime(expiry) + ", " +
                CLOSE_NAME + "=" + Format.toFrenchDateTime(closeDateTime) + ", " +
                STATUS_NAME + "=" + status + ", " +
                SIDE_NAME + "=" + (side ? "LONG" : "SHORT") + ", " +
                OPENING_PRICE_NAME + "=" + openingPrice + ", " +
                CLOSING_PRICE_NAME + "=" + closingPrice + ", " +
                TP_NAME + "=" + takeProfit + ", " +
                SL_NAME + stopLoss + ", " +
                LEVERAGE_NAME + getLeverage() + ", " +
                TP_PRICE_PER_NAME + "=" + this.getTakeProfitPricePercentage() + ", " +
                SL_PRICE_PER_NAME + "=" + this.getStopLossPricePercentage() + ", " +
                EXPECTED_PROFIT_NAME + "=" + this.getExpectedProfit() + ", " +
                RISK_PER_NAME + "=" + this.getRiskPercentage() + ", " +
                RR_RATIO_NAME + "=" + this.getRewardToRiskRatio() + ", " +
                PNL_NAME + "=" + this.getPnL() + "}";
    }


    @Override
    public List<Serializable> toRow() {
        return new ArrayList<>(List.of(
                Format.toFrenchDateTime(openDateTime),
                Format.toFrenchDateTime(lastUpdate),
                platform,
                getSymbol(),
                getTimeframe(),
                accountBalanceAtOpen,
                size,
                Format.toFrenchDateTime(expectedClose),
                Format.toFrenchDateTime(expiry),
                Format.toFrenchDateTime(closeDateTime),
                status,
                (side ? "LONG" : "SHORT"),
                openingPrice,
                closingPrice,
                takeProfit,
                stopLoss,
                getLeverage(),
                getTakeProfitPricePercentage(),
                getStopLossPricePercentage(),
                getExpectedProfit(),
                getRiskPercentage(),
                getRewardToRiskRatio(),
                getPnL()
        ));


    }

    public static Trade blank() {
        return new Trade();
    }

    public List<String> extractCsvHeader() {
        return new ArrayList<>(List.of(
                OPEN_NAME,
                LAST_UPDATE_NAME,
                PLATFORM_NAME,
                SYMBOL_NAME,
                TIMEFRAME_NAME,
                ACCOUNT_BALANCE_NAME,
                SIZE_NAME,
                EXPECTED_CLOSE_NAME,
                EXPIRY_NAME,
                CLOSE_NAME,
                STATUS_NAME,
                SIDE_NAME,
                OPENING_PRICE_NAME,
                CLOSING_PRICE_NAME,
                TP_NAME,
                SL_NAME,
                LEVERAGE_NAME,
                TP_PRICE_PER_NAME,
                SL_PRICE_PER_NAME,
                EXPECTED_PROFIT_NAME,
                RISK_PER_NAME,
                RR_RATIO_NAME,
                PNL_NAME
        ));
    }

    public void close(LocalDateTime closeDateTime, double closingPrice, TradeStatus status) {

        if (this.status == TradeStatus.OPENED) {
            this.closingPrice = closingPrice;
            this.closeDateTime = closeDateTime;
            this.status = status;
            this.lastUpdate = LocalDateTime.now();
        }
    }

    public void setExpiry(LocalDateTime expiry) {

        if ((status == TradeStatus.OPENED)
                && (expiry.isAfter(openDateTime))) {
            this.expiry = expiry;
            this.lastUpdate = LocalDateTime.now();
        }
    }

    /**
     * @param takeProfit zero means "no take profit"
     */
    public void setTakeProfit(double takeProfit) {

        if (status == TradeStatus.OPENED) {
            takeProfit = Format.roundTwoDigits(takeProfit);

            this.takeProfit = side ?
                    Format.streamline(takeProfit, openingPrice, Double.MAX_VALUE)
                    : Format.streamline(takeProfit, 0, openingPrice);

            this.lastUpdate = LocalDateTime.now();
        }
    }

    /**
     * @param stopLoss zero means "no stop loss"
     */
    public void setStopLoss(double stopLoss) {

        if (status == TradeStatus.OPENED) {
            stopLoss = Format.roundTwoDigits(stopLoss);

            this.stopLoss = side ?
                    Format.streamline(stopLoss, 0, openingPrice)
                    : Format.streamline(stopLoss, openingPrice, Double.MAX_VALUE);

            this.lastUpdate = LocalDateTime.now();
        }
    }

    public double getTakeProfitPricePercentage() {
        return Format.roundTwoDigits(abs(Calc.variationPercentage(this.takeProfit, this.openingPrice)));
    }

    public double getStopLossPricePercentage() {
        return Format.roundTwoDigits(abs(Calc.variationPercentage(this.stopLoss, this.openingPrice)));
    }

    public double getExpectedProfit() {

        double expectedProfit = 0;

        if (!(side && takeProfit == 0)) {
            expectedProfit = abs(takeProfit - openingPrice) * size;
        }

        return Format.roundTwoDigits(expectedProfit);
    }

    public double getMaxLoss() {
        return Format.roundTwoDigits(abs(stopLoss - openingPrice) * size);
    }

    private int getRiskPercentage() {
        return Calc.positivePercentage(getMaxLoss(), accountBalanceAtOpen);
    }

    public double getRewardToRiskRatio() {

        double maxLoss = this.getMaxLoss();

        double ratio = maxLoss == 0 ? 0 : this.getExpectedProfit() / maxLoss;

        return Format.roundNDigits(ratio, 1);
    }

    private float getLeverage() {
        return (float) Format.roundNDigits((getSize() * openingPrice) / accountBalanceAtOpen, 3);
    }

    public double getPnL() {

        double pnl = 0;

        if (status != TradeStatus.OPENED) {

            pnl = side ? closingPrice - openingPrice : openingPrice - closingPrice;
            pnl = Format.roundTwoDigits(pnl * size);
        }
        return pnl;
    }

    public double getReturnPercentage() {
        return Format.roundTwoDigits((getPnL() / accountBalanceAtOpen) * 100);
    }
}

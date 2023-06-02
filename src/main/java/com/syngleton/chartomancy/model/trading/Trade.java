package com.syngleton.chartomancy.model.trading;

import com.syngleton.chartomancy.model.charting.misc.*;
import com.syngleton.chartomancy.service.TradeStatus;
import com.syngleton.chartomancy.util.Calc;
import com.syngleton.chartomancy.util.Format;
import com.syngleton.chartomancy.util.Measurable;
import com.syngleton.chartomancy.util.pdt.PrintableData;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

@Getter
public class Trade extends ChartObject implements PrintableData {

    private static final String OPEN_NAME = "Open";
    private static final String LAST_UPDATE_NAME = "Last update";
    private static final String PLATFORM_NAME = "Platform";
    private static final String SYMBOL_NAME = "Symbol";
    private static final String TIMEFRAME_NAME = "Timeframe";
    private static final String ACCOUNT_BALANCE_NAME = "Account balance at open";
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
    
    private static final int DEFAULT_TP_TO_SL_RATIO = 3;
    private static final int DEFAULT_RISK_PERCENTAGE = 2;
    private static final long MAX_TRADE_DURATION_IN_SECONDS = Timeframe.WEEK.durationInSeconds * 4;

    private LocalDateTime lastUpdate;
    private final String platform;
    private final double accountBalanceAtOpen;
    private final int riskPercentage;
    private final double leverageX;
    private final boolean side;
    private final double openingPrice;
    private final LocalDateTime open;
    private final LocalDateTime expectedClose;
    private LocalDateTime expiry;
    private LocalDateTime close;
    private TradeStatus status;
    private double closingPrice;
    private double takeProfit;
    private double stopLoss;

    /**
     * Calls the constructor with default values for riskPercentage, and sets takeProfit and stopLoss
     * based on this default percentage, the DEFAULT_TP_TO_SL_RATIO and the accountBalanceAtOpen
     */
    public Trade(String platform,
                 Timeframe timeframe,
                 Symbol symbol,
                 Measurable account,
                 LocalDateTime open,
                 LocalDateTime expectedClose,
                 boolean side,
                 double openingPrice,
                 float leverageX) {

        this(platform,
                timeframe,
                symbol,
                account,
                open,
                DEFAULT_RISK_PERCENTAGE,
                expectedClose,
                side,
                openingPrice,
                -1,
                -1,
                leverageX);
    }

    /**
     * Calls the full-parameterized constructor
     * @param platform
     * @param timeframe
     * @param symbol
     * @param account
     * @param open
     * @param riskPercentage
     * @param expectedClose
     * @param side
     * @param openingPrice
     * @param takeProfit
     * @param stopLoss
     * @param leverageX
     */
    public Trade(String platform,
                 Timeframe timeframe,
                 Symbol symbol,
                 Measurable account,
                 LocalDateTime open,
                 int riskPercentage,
                 LocalDateTime expectedClose,
                 boolean side,
                 double openingPrice,
                 double takeProfit,
                 double stopLoss,
                 float leverageX) {

        super(symbol, timeframe);

        this.status = TradeStatus.OPENED;
        this.riskPercentage = riskPercentage < 0 ? DEFAULT_RISK_PERCENTAGE : riskPercentage;

        setStopLoss(stopLoss);
        setTakeProfit(takeProfit);

        this.platform = platform == null ? "unknown" : platform;
        this.accountBalanceAtOpen = account.getMeasure();
        this.open = open == null ? LocalDateTime.now() : open;
        this.expectedClose = expectedClose == null ?
                LocalDateTime.now().plusSeconds(timeframe.durationInSeconds * timeframe.scope)
                : expectedClose;
        this.expiry = LocalDateTime.now().plusSeconds(MAX_TRADE_DURATION_IN_SECONDS);
        this.close = null;
        this.side = side;
        this.openingPrice = openingPrice;
        this.leverageX = Format.streamline(leverageX, 0, 10);

        if (getSize() > accountBalanceAtOpen)   {
            this.status = TradeStatus.UNFUNDED;
        }

        this.lastUpdate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Trade{" +
                OPEN_NAME + "=" + open + ", " +
                LAST_UPDATE_NAME + "=" + this.lastUpdate + ", " +
                PLATFORM_NAME + "=" + getPlatform() + ", " +
                SYMBOL_NAME + "=" + getSymbol() + ", " +
                TIMEFRAME_NAME + "=" + getTimeframe() + ", " +
                ACCOUNT_BALANCE_NAME + "=" + accountBalanceAtOpen + ", " +
                EXPECTED_CLOSE_NAME + "=" + expectedClose + ", " +
                EXPIRY_NAME + "=" + expiry + ", " +
                CLOSE_NAME + "=" + close + ", " +
                STATUS_NAME + "=" + status + ", " +
                SIDE_NAME + "=" + (side ? "LONG" : "SHORT") + ", " +
                OPENING_PRICE_NAME + "=" + openingPrice + ", " +
                CLOSING_PRICE_NAME + "=" + closingPrice + ", " +
                TP_NAME + "=" + takeProfit + ", " +
                SL_NAME + stopLoss + ", " +
                LEVERAGE_NAME + leverageX + ", " +
                TP_PRICE_PER_NAME + "=" + this.getTakeProfitPricePercentage() + ", " +
                SL_PRICE_PER_NAME + "=" + this.getStopLossPricePercentage() + ", " +
                EXPECTED_PROFIT_NAME + "=" + this.getExpectedProfit() + ", " +
                RISK_PER_NAME + "=" + this.riskPercentage + ", " +
                SIDE_NAME + "=" + this.getSize() + ", " +
                RR_RATIO_NAME + "=" + this.getRewardToRiskRatio() + ", " +
                PNL_NAME + "=" + this.getPnL() + "}";
    }

    @Override
    public List<Serializable> toRow()   {
        return new ArrayList<>(List.of(
                open,
                lastUpdate,
                platform,
                getSymbol(),
                getTimeframe(),
                accountBalanceAtOpen,
                expectedClose,
                expiry,
                close == null ? "Not closed yet." : close,
                status,
                (side ? "LONG" : "SHORT"),
                openingPrice,
                closingPrice,
                takeProfit,
                stopLoss,
                leverageX,
                getTakeProfitPricePercentage(),
                getStopLossPricePercentage(),
                getExpectedProfit(),
                riskPercentage,
                getSize(),
                getRewardToRiskRatio(),
                getPnL()
        ));


    }

    public List<String> extractCsvHeader()   {
        return new ArrayList<>(List.of(OPEN_NAME,
                LAST_UPDATE_NAME,
                PLATFORM_NAME,
                SYMBOL_NAME,
                TIMEFRAME_NAME,
                ACCOUNT_BALANCE_NAME,
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
                SIDE_NAME,
                RR_RATIO_NAME,
                PNL_NAME
        ));
    }

    public void closeTrade(double closingPrice, TradeStatus status) {
        this.closingPrice = closingPrice;
        this.status = status;
        this.close = LocalDateTime.now();
    }

    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
        this.lastUpdate = LocalDateTime.now();
    }

    /**
     * @param takeProfit if is negative, take profit is set to price +/- riskPercentage * DEFAULT_TP_TO_SL_RATIO ; if equals zero, is set to zero (no take profit)
     */
    public void setTakeProfit(double takeProfit) {

        if (status == TradeStatus.OPENED) {
            takeProfit = Format.roundAccordingly(takeProfit);

            if ((takeProfit != 0) && (
                    (takeProfit < 0)
                            || ((side && takeProfit <= openingPrice)
                            || (!side && takeProfit >= openingPrice)
                    ))
            ) {
                double reward = Format.roundAccordingly((accountBalanceAtOpen * riskPercentage * DEFAULT_TP_TO_SL_RATIO) / 100);

                takeProfit = side ? openingPrice + reward : openingPrice - reward;
            }
            this.takeProfit = takeProfit;
            this.lastUpdate = LocalDateTime.now();
        }
    }

    /**
     * @param stopLoss if is negative, stop loss is set to price +/- riskPercentage ; if equals zero, is set to zero (no stop loss)
     */
    public void setStopLoss(double stopLoss) {

        if (status == TradeStatus.OPENED) {
            stopLoss = Format.roundAccordingly(stopLoss);

            double risk = Format.roundAccordingly((accountBalanceAtOpen * riskPercentage) / 100);

            if ((stopLoss != 0) && (
                    (stopLoss < 0)
                            || (side && stopLoss >= openingPrice)
                            || (!side && stopLoss <= openingPrice)
                            || (
                            (risk != 0)
                                    &&
                                    (abs(openingPrice - stopLoss) > risk)
                    ))) {
                stopLoss = side ? openingPrice - risk : openingPrice + risk;
            }
            this.stopLoss = stopLoss;
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

        if (!(this.side && takeProfit == 0)) {
            expectedProfit = abs(this.takeProfit - this.openingPrice) * this.leverageX;
        }

        return Format.roundAccordingly(expectedProfit, this.openingPrice);
    }

    public double getSize() {

        double size = 0;

        if (!(!this.side && stopLoss == 0)) {
            size = abs(this.stopLoss - this.openingPrice) * this.leverageX;
        }

        return Format.roundAccordingly(size, this.openingPrice);
    }

    public double getRewardToRiskRatio() {

        double maxLoss = this.getSize();

        return maxLoss == 0 ? 0 : Format.roundTwoDigits(this.getExpectedProfit() / maxLoss);
    }

    public double getPnL() {

        if (status != TradeStatus.OPENED) {
            return (side ? closingPrice - openingPrice : openingPrice - closingPrice) * leverageX;
        }
        return 0;
    }
}

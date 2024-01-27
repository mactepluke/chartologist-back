package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.charting_types.Symbol;
import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.shared_domain.Accountable;
import co.syngleton.chartomancer.shared_domain.ChartObject;
import co.syngleton.chartomancer.util.Calc;
import co.syngleton.chartomancer.util.Format;
import co.syngleton.chartomancer.util.datatabletool.PrintableData;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.min;

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
    private static final String FEE_PERCENTAGE_NAME = "Fee %";
    private static final String FEE_AMOUNT_NAME = "Fee Amount";
    private static final String ORDER_TYPE_NAME = "Order Type";

    private static final long MAX_TRADE_DURATION_IN_SECONDS = Timeframe.WEEK.durationInSeconds * 4;
    private static final int MAX_LEVERAGE = 20;

    private final String platform;
    private final double accountBalanceAtOpen;
    private final double size;
    private final boolean side;
    private final double openingPrice;
    private final LocalDateTime openDateTime;
    private final LocalDateTime expectedClose;
    private LocalDateTime lastUpdate;
    private LocalDateTime expiry;
    private LocalDateTime closeDateTime;
    private TradeStatus status;
    private double closingPrice;
    private double takeProfit;
    private double stopLoss;
    private double feePercentage;
    private boolean maker;

    private Trade() {
        super(null);
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
     *
     * @param platform      name of the exchange or the dummy graph to trade against
     * @param timeframe
     * @param symbol
     * @param account       account from which to read the balance
     * @param openDateTime
     * @param size          this is the token quantity (not the price of the tokens!)
     * @param expectedClose
     * @param side          long if true, short if false
     * @param openingPrice
     * @param takeProfit
     * @param stopLoss
     */
    public Trade(String platform, Timeframe timeframe, Symbol symbol, @NonNull Accountable account, LocalDateTime openDateTime, double size, LocalDateTime expectedClose, boolean side, double openingPrice, double takeProfit, double stopLoss, double feePercentage, boolean maker) {

        super(symbol, timeframe);

        this.maker = maker;
        this.status = (this.maker) ? TradeStatus.LIMIT_ORDER : TradeStatus.OPENED;
        this.openingPrice = openingPrice;
        this.side = side;

        setStopLoss(stopLoss);
        setTakeProfit(takeProfit);

        this.platform = platform == null ? "unknown" : platform;
        this.accountBalanceAtOpen = Format.roundTwoDigits(account.getBalance());
        this.openDateTime = openDateTime == null ? LocalDateTime.now() : openDateTime;
        this.expectedClose = expectedClose == null ? LocalDateTime.now().plusSeconds(timeframe.durationInSeconds * timeframe.scope) : expectedClose;
        this.expiry = LocalDateTime.now().plusSeconds(MAX_TRADE_DURATION_IN_SECONDS);
        this.closeDateTime = null;

        this.size = Format.roundNDigits(size, 3);
        this.feePercentage = feePercentage;

        if (getMaxLoss() > accountBalanceAtOpen) {
            log.debug("------------------>> maxloss={}, account balance at open ={}", getMaxLoss(), accountBalanceAtOpen);
            this.status = TradeStatus.UNFUNDED;
        }

        if (getTakeProfit() == openingPrice || getStopLoss() == openingPrice || getExpectedProfit() < 0) {
            log.debug("take profit={}, stop loss={}, expected profit={}", getTakeProfit(), getStopLoss(), getExpectedProfit());
            this.status = TradeStatus.BLANK;
        }

        this.lastUpdate = LocalDateTime.now();
    }

    @Contract(" -> new")
    public static @NonNull Trade blank() {
        return new Trade();
    }

    public boolean isOpen() {
        return status == TradeStatus.OPENED;
    }

    /**
     * @param stopLoss zero means "no stop loss"
     */
    public void setStopLoss(double stopLoss) {

        if (status == TradeStatus.OPENED || status == TradeStatus.LIMIT_ORDER) {
            stopLoss = Format.roundTwoDigits(stopLoss);

            if (this.side && stopLoss > this.openingPrice) {
                stopLoss = 0;
            }

            if (!this.side && stopLoss < this.openingPrice) {
                stopLoss = 0;
            }

            this.stopLoss = stopLoss;

            this.lastUpdate = LocalDateTime.now();
        }
    }

    /**
     * @param takeProfit zero means "no take profit"
     */
    public void setTakeProfit(double takeProfit) {

        if (status == TradeStatus.OPENED || status == TradeStatus.LIMIT_ORDER) {
            takeProfit = Format.roundTwoDigits(takeProfit);

            if (this.side && takeProfit < this.openingPrice) {
                takeProfit = 0;
            }

            if (!this.side && takeProfit > this.openingPrice) {
                takeProfit = 0;
            }

            this.takeProfit = takeProfit;

            this.lastUpdate = LocalDateTime.now();
        }
    }

    public double getMaxLoss() {
        return min(Format.roundTwoDigits(abs(stopLoss - openingPrice) * size - getFeeAmount()), this.accountBalanceAtOpen);
    }

    public double getFeeAmount() {
        return abs((size * openingPrice * feePercentage) / 100);
    }

    public long getTradeDurationInSeconds() {

        LocalDateTime endTime;

        if (status == TradeStatus.OPENED) {
            endTime = expectedClose;
        } else {
            endTime = closeDateTime;
        }
        return endTime.toEpochSecond(ZoneOffset.UTC) - openDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    public void orderLimitHit() {
        if (status == TradeStatus.LIMIT_ORDER) {
            status = TradeStatus.OPENED;
        }
    }

    @Override
    public String toString() {
        return "Trade{" + OPEN_NAME + "=" + Format.toFrenchDateTime(openDateTime) + ", " + LAST_UPDATE_NAME + "=" + Format.toFrenchDateTime(lastUpdate) + ", " + PLATFORM_NAME + "=" + getPlatform() + ", " + SYMBOL_NAME + "=" + getSymbol() + ", " + TIMEFRAME_NAME + "=" + getTimeframe() + ", " + ACCOUNT_BALANCE_NAME + "=" + accountBalanceAtOpen + ", " + SIZE_NAME + "=" + size + ", " + EXPECTED_CLOSE_NAME + "=" + Format.toFrenchDateTime(expectedClose) + ", " + EXPIRY_NAME + "=" + Format.toFrenchDateTime(expiry) + ", " + CLOSE_NAME + "=" + Format.toFrenchDateTime(closeDateTime) + ", " + STATUS_NAME + "=" + status + ", " + SIDE_NAME + "=" + (side ? "LONG" : "SHORT") + ", " + OPENING_PRICE_NAME + "=" + openingPrice + ", " + CLOSING_PRICE_NAME + "=" + closingPrice + ", " + TP_NAME + "=" + takeProfit + ", " + SL_NAME + stopLoss + ", " + LEVERAGE_NAME + getLeverage() + ", " + TP_PRICE_PER_NAME + "=" + this.getTakeProfitPricePercentage() + ", " + SL_PRICE_PER_NAME + "=" + this.getStopLossPricePercentage() + ", " + EXPECTED_PROFIT_NAME + "=" + this.getExpectedProfit() + ", " + RISK_PER_NAME + "=" + this.getRiskPercentage() + ", " + RR_RATIO_NAME + "=" + this.getRewardToRiskRatio() + ", " + PNL_NAME + "=" + this.getPnL() + ", " + FEE_PERCENTAGE_NAME + "=" + feePercentage + ", " + FEE_AMOUNT_NAME + "=" + this.getFeeAmount() + ", " + ORDER_TYPE_NAME + "=" + (maker ? "MAKER" : "TAKER") + "}";
    }

    public float getLeverage() {
        return (float) Format.roundNDigits((getSize() * openingPrice) / accountBalanceAtOpen, 3);
    }

    public double getTakeProfitPricePercentage() {
        return Format.roundTwoDigits(abs(Calc.variationPercentage(this.takeProfit, this.openingPrice)));
    }

    public double getStopLossPricePercentage() {
        return Format.roundTwoDigits(abs(Calc.variationPercentage(this.stopLoss, this.openingPrice)));
    }

    public double getExpectedProfit() {

        double expectedProfit = 0;

        if (takeProfit != 0) {
            expectedProfit = abs(takeProfit - openingPrice) * size - getFeeAmount();
        }
        return Format.roundTwoDigits(expectedProfit);
    }

    private int getRiskPercentage() {
        return Calc.positivePercentage(getMaxLoss(), accountBalanceAtOpen);
    }

    public double getRewardToRiskRatio() {

        double maxLoss = this.getMaxLoss();

        double ratio = maxLoss == 0 ? 0 : this.getExpectedProfit() / maxLoss;

        return Format.roundNDigits(ratio, 1);
    }

    @Override
    public List<Serializable> toRow() {
        return new ArrayList<>(List.of(Format.toFrenchDateTime(openDateTime), Format.toFrenchDateTime(lastUpdate), platform, getSymbol(), getTimeframe(), accountBalanceAtOpen, size, Format.toFrenchDateTime(expectedClose), Format.toFrenchDateTime(expiry), Format.toFrenchDateTime(closeDateTime), status, (side ? "LONG" : "SHORT"), openingPrice, closingPrice, takeProfit, stopLoss, getLeverage(), getTakeProfitPricePercentage(), getStopLossPricePercentage(), getExpectedProfit(), getRiskPercentage(), getRewardToRiskRatio(), getPnL(), feePercentage, getFeeAmount(), (maker ? "MAKER" : "TAKER")));


    }

    public List<String> extractCsvHeader() {
        return new ArrayList<>(List.of(OPEN_NAME, LAST_UPDATE_NAME, PLATFORM_NAME, SYMBOL_NAME, TIMEFRAME_NAME, ACCOUNT_BALANCE_NAME, SIZE_NAME, EXPECTED_CLOSE_NAME, EXPIRY_NAME, CLOSE_NAME, STATUS_NAME, SIDE_NAME, OPENING_PRICE_NAME, CLOSING_PRICE_NAME, TP_NAME, SL_NAME, LEVERAGE_NAME, TP_PRICE_PER_NAME, SL_PRICE_PER_NAME, EXPECTED_PROFIT_NAME, RISK_PER_NAME, RR_RATIO_NAME, PNL_NAME, FEE_PERCENTAGE_NAME, FEE_AMOUNT_NAME, ORDER_TYPE_NAME));
    }

    public void hitStopLoss(LocalDateTime closeDateTime) {
        close(closeDateTime, stopLoss, TradeStatus.STOP_LOSS_HIT);
    }

    public void hitTakeProfit(LocalDateTime closeDateTime) {
        close(closeDateTime, takeProfit, TradeStatus.TAKE_PROFIT_HIT);
    }

    public void expire(LocalDateTime closeDateTime, float lastCandleClose) {
        close(closeDateTime, lastCandleClose, TradeStatus.EXPIRED);
    }

    public boolean isBlank() {
        return status == TradeStatus.BLANK;
    }

    public boolean isNotBlank() {
        return !isBlank();
    }

    public boolean isUnfunded() {
        return status == TradeStatus.UNFUNDED;
    }

    public boolean isClosed() {
        return status == TradeStatus.STOP_LOSS_HIT || status == TradeStatus.TAKE_PROFIT_HIT || status == TradeStatus.EXPIRED;
    }

    private void close(LocalDateTime closeDateTime, double closingPrice, TradeStatus status) {

        if (this.status == TradeStatus.OPENED) {
            this.closingPrice = closingPrice;
            this.closeDateTime = closeDateTime;
            this.status = status;
            this.lastUpdate = LocalDateTime.now();
        }
    }

    public void cancel() {

        if (this.status == TradeStatus.LIMIT_ORDER) {
            this.status = TradeStatus.CANCELED;
            this.lastUpdate = LocalDateTime.now();
        }
    }

    public void setExpiry(LocalDateTime expiry) {

        if ((status == TradeStatus.OPENED || status == TradeStatus.LIMIT_ORDER) && (expiry.isAfter(openDateTime))) {
            this.expiry = expiry;
            this.lastUpdate = LocalDateTime.now();
        }
    }

    public double getReturnPercentage() {
        return Format.roundTwoDigits((getPnL() / accountBalanceAtOpen) * 100);
    }

    public double getPnL() {

        double pnl = 0;

        if (status != TradeStatus.OPENED && status != TradeStatus.LIMIT_ORDER && status != TradeStatus.BLANK && status != TradeStatus.UNFUNDED && status != TradeStatus.CANCELED) {

            pnl = side ? closingPrice - openingPrice : openingPrice - closingPrice;
            pnl = Format.roundTwoDigits(pnl * size - getFeeAmount());

            if (accountBalanceAtOpen + pnl < 0) {
                pnl = this.accountBalanceAtOpen;
            }
        }
        return pnl;
    }
}

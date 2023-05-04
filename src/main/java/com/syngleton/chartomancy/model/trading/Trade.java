package com.syngleton.chartomancy.model.trading;

import com.syngleton.chartomancy.model.charting.misc.ChartObject;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.util.Calc;
import com.syngleton.chartomancy.util.Format;
import lombok.Getter;

import java.time.LocalDateTime;

import static java.lang.Math.abs;

@Getter
public class Trade extends ChartObject {

    private LocalDateTime lastUpdate;
    private final int leverage;
    private final boolean side;
    private final float openingPrice;
    private final LocalDateTime open;
    private final LocalDateTime expectedClose;
    private LocalDateTime close;
    private boolean opened;
    private float closingPrice;
    private float takeProfit;
    private float stopLoss;

    public Trade(Timeframe timeframe,
                 Symbol symbol,
                 LocalDateTime open,
                 LocalDateTime expectedClose,
                 boolean side,
                 float openingPrice,
                 float takeProfit,
                 float stopLoss,
                 int leverage)  {

        super(symbol, timeframe);

        stopLoss = Format.roundAccordingly(abs(stopLoss));

        if ((side && stopLoss >= openingPrice)
            || (!side && stopLoss <= openingPrice))   {
            stopLoss = openingPrice;
        }

        takeProfit = Format.roundAccordingly(abs(takeProfit));

        if ((side && takeProfit <= openingPrice)
        || (!side && takeProfit >= openingPrice)) {
            takeProfit = openingPrice;
        }

        this.open = open;
        this.expectedClose = expectedClose;
        this.close = null;
        this.opened = true;
        this.side = side;
        this.openingPrice = openingPrice;
        this.takeProfit = takeProfit;
        this.stopLoss = stopLoss;
        this.leverage = leverage;
        this.lastUpdate = LocalDateTime.now();
    }
//TODO Create additional fields to record results of the trade?
    @Override
    public String toString()  {
        return "Trade{" +
                "Last update=" + this.lastUpdate +
                ", symbol=" + getSymbol() +
                ", timeframe=" + getTimeframe() +
                ", open=" + open +
                ", expected_close=" + expectedClose +
                ", close=" + close +
                ", status=" + (opened ? "opened" : "closed") +
                ", side=" + (side ? "LONG" : "SHORT") +
                ", opening_price=" + openingPrice +
                ", closing_price=" + closingPrice +
                ", take_profit=" + takeProfit +
                ", stop_loss=" + stopLoss +
                ", leverage=" + leverage +
                ", TP_price_%=" + this.getTakeProfitPricePercentage() +
                ", SL_price_%=" + this.getStopLossPricePercentage() +
                ", exp_profit=" + this.getExpectedProfit() +
                ", max_loss=" + this.getMaxLoss() +
                ", RR_ratio=" + this.getRewardToRiskRatio() + "}";
    }

    public void setClose(LocalDateTime close) {
        this.close = close;
        this.lastUpdate = LocalDateTime.now();
    }

    public void setClosingPrice(float closingPrice) {
        this.closingPrice = closingPrice;
        this.lastUpdate = LocalDateTime.now();
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
        this.lastUpdate = LocalDateTime.now();
    }

    public void setTakeProfit(float takeProfit) {
        this.takeProfit = Format.roundAccordingly(takeProfit);

        if ((side && takeProfit <= openingPrice)
                || (!side && takeProfit >= openingPrice)) {
            takeProfit = openingPrice;
        }

        this.lastUpdate = LocalDateTime.now();
    }

    public void setStopLoss(float stopLoss) {
        this.stopLoss = Format.roundAccordingly(stopLoss);

        if ((side && stopLoss >= openingPrice)
                || (!side && stopLoss <= openingPrice))   {
            stopLoss = openingPrice;
        }

        this.lastUpdate = LocalDateTime.now();
    }

    public float getTakeProfitPricePercentage()    {
        return Format.roundTwoDigits(abs(Calc.variationPercentage(this.takeProfit, this.openingPrice)));
    }

    public float getStopLossPricePercentage()    {
        return Format.roundTwoDigits(abs(Calc.variationPercentage(this.stopLoss, this.openingPrice)));
    }

    public float getExpectedProfit()    {

        float expectedProfit = 0;

        if (!(this.side && takeProfit == 0))  {
            expectedProfit = abs(this.takeProfit - this.openingPrice) * this.leverage;
        }

        return Format.roundAccordingly(expectedProfit, this.openingPrice);
    }

    public float getMaxLoss()    {

        float maxLoss = 0;

        if (!(!this.side && stopLoss == 0))  {
            maxLoss = abs(this.stopLoss - this.openingPrice) * this.leverage;
        }

        return Format.roundAccordingly(maxLoss, this.openingPrice);
    }

    public float getRewardToRiskRatio() {

        float maxLoss = this.getMaxLoss();

        return maxLoss == 0 ? 0 : Format.roundTwoDigits(this.getExpectedProfit() / maxLoss);
    }
}

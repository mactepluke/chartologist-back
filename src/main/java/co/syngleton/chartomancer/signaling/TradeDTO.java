package co.syngleton.chartomancer.signaling;

import co.syngleton.chartomancer.charting_types.Timeframe;
import co.syngleton.chartomancer.trading.Trade;
import co.syngleton.chartomancer.trading.TradeStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
class TradeDTO {
    @JsonProperty
    private boolean blank;
    @JsonProperty
    private Timeframe timeframe;
    @JsonProperty
    private double size;
    @JsonProperty
    private String side;
    @JsonProperty
    private double openingPrice;
    @JsonProperty
    private LocalDateTime expectedClose;
    @JsonProperty
    private double takeProfit;
    @JsonProperty
    private double stopLoss;
    @JsonProperty
    private double expectedProfit;
    @JsonProperty
    private double riskToRewardRatio;
    @JsonProperty
    private double maxLoss;
    @JsonProperty
    private float leverage;


    public TradeDTO(Trade trade) {
        this.blank = trade.getStatus() == TradeStatus.BLANK;
        this.timeframe = trade.getTimeframe();
        this.size = trade.getSize();
        this.side = trade.isSide() ? "LONG" : "SHORT";
        this.openingPrice = trade.getOpeningPrice();
        this.expectedClose = trade.getExpectedClose();
        this.takeProfit = trade.getTakeProfit();
        this.stopLoss = trade.getStopLoss();
        this.expectedProfit = trade.getExpectedProfit();
        this.riskToRewardRatio = trade.getRewardToRiskRatio();
        this.maxLoss = trade.getMaxLoss();
        this.leverage = trade.getLeverage();
    }

}

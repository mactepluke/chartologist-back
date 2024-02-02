package co.syngleton.chartomancer.trading;

import co.syngleton.chartomancer.exception.InvalidParametersException;
import co.syngleton.chartomancer.util.Format;

import static java.lang.Math.abs;

class TradingAdvisorImpl implements TradingAdvisor {
    private final int rewardToRiskRatio;
    private final int riskPercentage;
    private final SL_TP_Strategy slTpStrategy;

    TradingAdvisorImpl(int rewardToRiskRatio, int riskPercentage, SL_TP_Strategy slTpStrategy) {
        this.rewardToRiskRatio = rewardToRiskRatio;
        this.riskPercentage = riskPercentage;
        this.slTpStrategy = slTpStrategy;
    }

    @Override
    public TradingAdvice getAdvice(double balance, float openingPrice, float expectedPriceVariation) {

        final float takeProfit;
        final float stopLoss;
        final double size;

        switch (slTpStrategy) {
            case NONE -> {
                takeProfit = 0;
                stopLoss = 0;
            }
            case SL_NO_TP -> {
                takeProfit = 0;
                stopLoss = calculateStopLossWithRR(openingPrice, expectedPriceVariation);
            }
            case TP_NO_SL -> {
                takeProfit = calculateTakeProfit(openingPrice, expectedPriceVariation);
                stopLoss = 0;
            }
            case EQUAL -> {
                takeProfit = calculateTakeProfit(openingPrice, expectedPriceVariation);
                stopLoss = calculateStopLossWithMultiplier(openingPrice, expectedPriceVariation, 1);
            }
            case SL_IS_2X_TP -> {
                takeProfit = calculateTakeProfit(openingPrice, expectedPriceVariation);
                stopLoss = calculateStopLossWithMultiplier(openingPrice, expectedPriceVariation, 2);
            }
            case SL_IS_3X_TP -> {
                takeProfit = calculateTakeProfit(openingPrice, expectedPriceVariation);
                stopLoss = calculateStopLossWithMultiplier(openingPrice, expectedPriceVariation, 3);
            }
            case BASIC_RR -> {
                takeProfit = calculateTakeProfit(openingPrice, expectedPriceVariation);
                stopLoss = calculateStopLossWithRR(openingPrice, expectedPriceVariation);
            }
            default -> throw new InvalidParametersException("SL_TP_Strategy is unspecified.");
        }

        size = calculateSize(balance, stopLoss, openingPrice);

        return new TradingAdvice(takeProfit, stopLoss, size);
    }

    private float calculateTakeProfit(float openingPrice, float expectedPriceVariation) {
        return Format.roundTwoDigits(openingPrice + (openingPrice * expectedPriceVariation) / 100);
    }

    private float calculateStopLossWithRR(float openingPrice, float expectedPriceVariation) {
        return Format.roundTwoDigits(openingPrice - (openingPrice * expectedPriceVariation / this.rewardToRiskRatio) / 100);
    }

    private float calculateStopLossWithMultiplier(float openingPrice, float expectedPriceVariation, int multiplier) {
        return Format.roundTwoDigits(openingPrice - (openingPrice * expectedPriceVariation) * multiplier / 100);
    }

    private double calculateSize(double balance, float stopLoss, float openingPrice) {
        return ((balance * this.riskPercentage) / 100) / abs(stopLoss - openingPrice);
    }

}

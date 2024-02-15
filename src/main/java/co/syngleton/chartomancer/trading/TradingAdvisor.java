package co.syngleton.chartomancer.trading;

interface TradingAdvisor {

    static TradingAdvisor getNewInstance(int rewardToRiskRatio, int riskPercentage, SL_TP_Strategy slTpStrategy) {
        return new TradingAdvisorImpl(rewardToRiskRatio, riskPercentage, slTpStrategy);
    }

    TradingAdvice getAdvice(double balance, float openingPrice, float expectedPriceVariation);
}

package com.syngleton.chartomancy.automation;

import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;
import com.syngleton.chartomancy.analytics.Smoothing;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.charting.misc.PatternType;
import com.syngleton.chartomancy.model.charting.misc.Symbol;
import com.syngleton.chartomancy.model.charting.misc.Timeframe;
import com.syngleton.chartomancy.model.trading.ProfitFactor;
import com.syngleton.chartomancy.model.trading.TradingSettings;
import com.syngleton.chartomancy.util.datatabletool.PrintableData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public record DummyTradesSummaryEntry(String dummyTradesDateTime,
                                      String computationDateTime,
                                      String csvTradesHistoryFileName,
                                      String dummyTradesSummaryCsvFileName,
                                      Symbol symbol,
                                      Timeframe timeframe,
                                      Smoothing matchScoreSmoothing,
                                      int matchScoreThreshold,
                                      float priceVariationThreshold,
                                      boolean extrapolatePriceVariation,
                                      boolean extrapolateMatchScore,
                                      PatternSettings.Autoconfig patternAutoconfig,
                                      ComputationSettings.Autoconfig computationAutoconfig,
                                      ComputationType computationType,
                                      PatternType computationPatternType,
                                      boolean atomicPartition,
                                      int scope,
                                      boolean fullScope,
                                      int patternLength,
                                      int patternGranularity,
                                      double riskToRewardRatio,
                                      int riskPercentage,
                                      float tradingPriceVariationThreshold,
                                      int priceVariationMultiplier,
                                      TradingSettings.SL_TP_Strategy slTpStrategy,
                                      int maxTrades,
                                      String tradesResult,
                                      double initialAccountBalance,
                                      double targetAccountBalance,
                                      double finalAccountBalance,
                                      double minimumAccountBalance,
                                      long numberOfTrades,
                                      long numberOfLongs,
                                      long numberOfShorts,
                                      long numberOfUselessTrades,
                                      float usedToUselessTradesRatio,
                                      double totalPnL,
                                      double longPnL,
                                      double shortPnL,
                                      double totalWinToLossRatio,
                                      double longWinToLassRatio,
                                      double shortWinToLongRatio,
                                      double averageTotalPnL,
                                      double averageLongPnL,
                                      double averageShortPnL,
                                      double averageTotalReturn,
                                      double averageLongReturn,
                                      double averageShortReturn,
                                      double profitFactor,
                                      ProfitFactor profitFactorQualification) implements PrintableData {

    @Override
    public List<Serializable> toRow() {
        return new ArrayList<>(List.of(
                dummyTradesDateTime,
                computationDateTime,
                csvTradesHistoryFileName,
                dummyTradesSummaryCsvFileName,
                symbol,
                timeframe,
                matchScoreSmoothing,
                matchScoreThreshold,
                priceVariationThreshold,
                extrapolatePriceVariation,
                extrapolateMatchScore,
                patternAutoconfig,
                computationAutoconfig,
                computationType,
                computationPatternType,
                atomicPartition,
                scope,
                fullScope,
                patternLength,
                patternGranularity,
                riskToRewardRatio,
                riskPercentage,
                tradingPriceVariationThreshold,
                priceVariationMultiplier,
                slTpStrategy,
                maxTrades,
                tradesResult,
                initialAccountBalance,
                targetAccountBalance,
                finalAccountBalance,
                minimumAccountBalance,
                numberOfTrades,
                numberOfLongs,
                numberOfShorts,
                numberOfUselessTrades,
                usedToUselessTradesRatio,
                totalPnL,
                longPnL,
                shortPnL,
                totalWinToLossRatio,
                longWinToLassRatio,
                shortWinToLongRatio,
                averageTotalPnL,
                averageLongPnL,
                averageShortPnL,
                averageTotalReturn,
                averageLongReturn,
                averageShortReturn,
                profitFactor,
                profitFactorQualification
        ));
    }

}

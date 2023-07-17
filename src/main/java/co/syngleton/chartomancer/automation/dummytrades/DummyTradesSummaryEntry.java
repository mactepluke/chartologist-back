package co.syngleton.chartomancer.automation.dummytrades;

import co.syngleton.chartomancer.analytics.ComputationType;
import co.syngleton.chartomancer.analytics.Smoothing;
import co.syngleton.chartomancer.model.charting.misc.PatternType;
import co.syngleton.chartomancer.model.charting.misc.Symbol;
import co.syngleton.chartomancer.model.charting.misc.Timeframe;
import co.syngleton.chartomancer.analytics.ComputationSettings;
import co.syngleton.chartomancer.factory.PatternSettings;
import co.syngleton.chartomancer.model.trading.ProfitFactor;
import co.syngleton.chartomancer.model.trading.TradingSettings;
import co.syngleton.chartomancer.util.datatabletool.PrintableData;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;

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
                                      Smoothing tradingMatchScoreSmoothing,
                                      int tradingMatchScoreThreshold,
                                      float tradingPriceVariationThreshold,
                                      boolean tradingExtrapolatePriceVariation,
                                      boolean tradingExtrapolateMatchScore,
                                      double riskToRewardRatio,
                                      int riskPercentage,
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
                                      ProfitFactor profitFactorQualification,
                                      double totalDuration,
                                      double annualizedReturnPercentage) implements PrintableData {

    @Contract(" -> new")
    @Override
    public @NonNull List<Serializable> toRow() {
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
                tradingMatchScoreSmoothing,
                tradingMatchScoreThreshold,
                tradingPriceVariationThreshold,
                tradingExtrapolatePriceVariation,
                tradingExtrapolateMatchScore,
                riskToRewardRatio,
                riskPercentage,
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
                profitFactorQualification,
                totalDuration,
                annualizedReturnPercentage
        ));
    }

}

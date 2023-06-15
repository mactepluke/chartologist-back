package com.syngleton.chartomancy.automation;

import com.syngleton.chartomancy.util.datatabletool.PrintableData;
import com.syngleton.chartomancy.util.datatabletool.PrintableDataTable;

import java.util.ArrayList;
import java.util.List;

public class DummyTradesSummaryTable implements PrintableDataTable {
    private static final String DUMMY_TRADES_DATE_TIME = "Dummy Trades Date Time";
    private static final String DATA_SER_VERSION_NAME = "Data.ser version name";
    private static final String CSV_TRADES_HISTORY_FILE_NAME = "CSV Trades History file name";
    private static final String DUMMY_TRADES_SUMMARY_CSV_FILE_NAME = "Dummy Trades Summary CSV file name";
    private static final String SYMBOL = "Symbol";
    private static final String TIMEFRAME = "Timeframe";
    private static final String MS_SMOOTHING = "Match Score Smoothing";
    private static final String MS_THRESHOLD = "Match Score Threshold";
    private static final String PRICE_VAR_THRESHOLD = "Price Variation Threshold";
    private static final String EXTRAPOLATE_PRICE_VAR = "Extrapolate Price Variation";
    private static final String EXTRAPOLATE_MS = "Extrapolate Match Score";
    private static final String PATTERN_AUTOCONFIG = "Pattern Autoconfig";
    private static final String COMPUTATION_AUTOCONFIG = "Computation Autoconfig";
    private static final String COMPUTATION_TYPE = "Computation Type";
    private static final String COMPUTATION_PATTERN_TYPE = "Computation Pattern Type";
    private static final String ATOMIC_PARTITION = "Atomic Partition";
    private static final String SCOPE = "Scope";
    private static final String FULL_SCOPE = "Full Scope";
    private static final String PATTERN_LENGTH = "Pattern Length";
    private static final String PATTERN_GRANULARITY = "Pattern Granularity";
    private static final String RR_RATIO = "Reward to Risk ratio";
    private static final String RISK_PERCENTAGE = "Risk Percentage";
    private static final String TRADING_PRICE_VAR_THRESHOLD = "Trading Price Variation Threshold";
    private static final String PRICE_VAR_MULTIPLIER = "Price Variation Multiplier";
    private static final String SL_TP_STRATEGY = "SL_TP Strategy";
    private static final String MAX_TRADES = "Max Trades";
    private static final String TRADES_RESULT = "Trades Result";
    private static final String INITIAL_ACCOUNT_BAL = "Initial Account Balance";
    private static final String TARGET_ACCOUNT_BAL = "Target Account Balance";
    private static final String FINAL_ACCOUNT_BAL = "Final Account Balance";
    private static final String MINIMUM_ACCOUNT_BAL = "Minimum Account Balance";
    private static final String NUMBER_OF_TRADES = "Number of Trades performed";
    private static final String NUMBER_OF_LONGS = "Number of Longs";
    private static final String NUMBER_OF_SHORTS = "Number of Shorts";
    private static final String NUMBER_OF_USELESS = "Number of Useless Trades";
    private static final String USED_TO_USELESS_RATIO = "Used to Useless Trade ratio";
    private static final String TOTAL_PNL = "Total PnL";
    private static final String LONG_PNL = "Long PnL";
    private static final String SHORT_PNL = "Short PnL";
    private static final String TOTAL_WTL_RATIO = "Total Win to Loss ratio";
    private static final String LONG_WTL_RATIO = "Long Win to Loss ratio";
    private static final String SHORT_WTL_RATIO = "Short Win to Loss ratio";
    private static final String AVERAGE_TOTAL_PNL = "Average Total PnL";
    private static final String AVERAGE_LONG_PNL = "Average Long PnL";
    private static final String AVERAGE_SHORT_PNL = "Average Short PnL";
    private static final String AVERAGE_TOTAL_RETURN = "Average Total Return";
    private static final String AVERAGE_LONG_RETURN = "Average Long Return";
    private static final String AVERAGE_SHORT_RETURN = "Average Short Return";
    private static final String PROFIT_FACTOR = "Profit Factor";
    private static final String PROFIT_FACTOR_QUALIFICATION = "Profit Factor Qualification";

    List<PrintableData> dummyTradesSummary;

    public DummyTradesSummaryTable() {
        this.dummyTradesSummary = new ArrayList<>();
    }

    @Override
    public List<String> getHeader() {
        return new ArrayList<>(List.of(
                DUMMY_TRADES_DATE_TIME,
                DATA_SER_VERSION_NAME,
                CSV_TRADES_HISTORY_FILE_NAME,
                DUMMY_TRADES_SUMMARY_CSV_FILE_NAME,
                SYMBOL,
                TIMEFRAME,
                MS_SMOOTHING,
                MS_THRESHOLD,
                PRICE_VAR_THRESHOLD,
                EXTRAPOLATE_PRICE_VAR,
                EXTRAPOLATE_MS,
                PATTERN_AUTOCONFIG,
                COMPUTATION_AUTOCONFIG,
                COMPUTATION_TYPE,
                COMPUTATION_PATTERN_TYPE,
                ATOMIC_PARTITION,
                SCOPE,
                FULL_SCOPE,
                PATTERN_LENGTH,
                PATTERN_GRANULARITY,
                RR_RATIO,
                RISK_PERCENTAGE,
                TRADING_PRICE_VAR_THRESHOLD,
                PRICE_VAR_MULTIPLIER,
                SL_TP_STRATEGY,
                MAX_TRADES,
                TRADES_RESULT,
                INITIAL_ACCOUNT_BAL,
                TARGET_ACCOUNT_BAL,
                FINAL_ACCOUNT_BAL,
                MINIMUM_ACCOUNT_BAL,
                NUMBER_OF_TRADES,
                NUMBER_OF_LONGS,
                NUMBER_OF_SHORTS,
                NUMBER_OF_USELESS,
                USED_TO_USELESS_RATIO,
                TOTAL_PNL,
                LONG_PNL,
                SHORT_PNL,
                TOTAL_WTL_RATIO,
                LONG_WTL_RATIO,
                SHORT_WTL_RATIO,
                AVERAGE_TOTAL_PNL,
                AVERAGE_LONG_PNL,
                AVERAGE_SHORT_PNL,
                AVERAGE_TOTAL_RETURN,
                AVERAGE_LONG_RETURN,
                AVERAGE_SHORT_RETURN,
                PROFIT_FACTOR,
                PROFIT_FACTOR_QUALIFICATION
        ));
    }

    @Override
    public List<PrintableData> getPrintableData() {
        return dummyTradesSummary;
    }

}

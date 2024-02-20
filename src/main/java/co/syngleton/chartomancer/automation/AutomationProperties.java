package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.charting_types.Timeframe;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "automation")
record AutomationProperties(
        @DefaultValue("false") boolean launchAutomation,
        @DefaultValue("false") boolean printCoreData,
        @DefaultValue("false") boolean printPricePredictionSummary,
        @DefaultValue("false") boolean runBasicDummyTrades,
        @DefaultValue("false") boolean runRandomizedDummyTrades,
        @DefaultValue("false") boolean runRandomizedDummyTradesOnDummyGraphs,
        @DefaultValue("false") boolean runDeterministicDummyTradesOnDummyGraphs,
        @DefaultValue("dummy_data") String dummyGraphsDataFolderName,
        @DefaultValue("[]") List<String> dummyGraphsDataFilesNames,
        @DefaultValue("[]") Set<Timeframe> dummyTradesTimeframes,
        @DefaultValue("10000") int dummyTradesInitialBalance,
        @DefaultValue("5000") int dummyTradesMinimumBalance,
        @DefaultValue("100") int dummyTradesExpectedBalanceX,
        @DefaultValue("5000") int dummyTradesMaxTrades,
        @DefaultValue("false") boolean writeDummyTradesReports,
        @DefaultValue("false") boolean printTasksHistory
) {
}

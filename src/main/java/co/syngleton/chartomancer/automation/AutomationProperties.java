package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "automation", ignoreUnknownFields = false)
@Getter
@Setter
public class AutomationProperties {
    private boolean launchAutomation;
    private boolean printCoreData;
    private boolean printPricePredictionSummary;
    private boolean runBasicDummyTrades;
    private boolean runRandomizedDummyTrades;
    private boolean runRandomizedDummyTradesOnDummyGraphs;
    private boolean runDeterministicDummyTradesOnDummyGraphs;
    private String dummyGraphsDataFolderName;
    private List<String> dummyGraphsDataFilesNames;
    private double dummyTradesInitialBalance;
    private double dummyTradesMinimumBalance;
    private int dummyTradesExpectedBalanceX;
    private int dummyTradesMaxTrades;
    private Set<Timeframe> dummyTradesTimeframes;
    private boolean writeDummyTradesReports;
    private boolean printTasksHistory;
}

package co.syngleton.chartomancer.automation;

import co.syngleton.chartomancer.charting_types.Timeframe;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "automation")
@Getter
@Setter
public class AutomationProperties {
    private boolean launchAutomation = false;
    private boolean printCoreData = false;
    private boolean printPricePredictionSummary = false;
    private boolean runBasicDummyTrades = false;
    private boolean runRandomizedDummyTrades = false;
    private boolean runRandomizedDummyTradesOnDummyGraphs = false;
    private boolean runDeterministicDummyTradesOnDummyGraphs = false;
    private String dummyGraphsDataFolderName;
    private List<String> dummyGraphsDataFilesNames;
    private double dummyTradesInitialBalance = 10000;
    private double dummyTradesMinimumBalance = 5000;
    private int dummyTradesExpectedBalanceX = 2;
    private int dummyTradesMaxTrades = 100;
    private Set<Timeframe> dummyTradesTimeframes = null;
    private boolean writeDummyTradesReports = false;
    private boolean printTasksHistory = false;
}

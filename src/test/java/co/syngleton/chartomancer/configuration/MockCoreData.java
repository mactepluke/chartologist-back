package co.syngleton.chartomancer.configuration;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.DefaultCoreData;
import co.syngleton.chartomancer.core_entities.PurgeOption;
import co.syngleton.chartomancer.data.DataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@TestConfiguration
public class MockCoreData {

    public static final String TEST_CORE_DATA_FILE_PATH = "./core_data/TEST_coredata.ser";
    private static final String TEST_PATH = "./";
    @Value("${data.folder_name}")
    private String testDataFolderName;
    @Value("#{'${automation.dummy_graphs_data_files_names}'.split(',')}")
    private List<String> testDummyGraphsDataFilesNames;

    @Autowired
    private DataProcessor dataProcessor;

    @Bean
    CoreData coreData() {

        final CoreData coreData = DefaultCoreData.newInstance();
        dataProcessor.loadCoreData(coreData, TEST_CORE_DATA_FILE_PATH);
        coreData.pushTradingPatternData();
        coreData.purgeUselessData(PurgeOption.GRAPHS_AND_PATTERNS);
        dataProcessor.loadGraphs(coreData, TEST_PATH + testDataFolderName + "/", testDummyGraphsDataFilesNames);
        dataProcessor.createGraphsForMissingTimeframes(coreData);

        return coreData;
    }
}

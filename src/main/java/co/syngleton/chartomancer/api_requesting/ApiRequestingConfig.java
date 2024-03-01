package co.syngleton.chartomancer.api_requesting;

import co.syngleton.chartomancer.core_entities.CoreData;
import co.syngleton.chartomancer.core_entities.DefaultCoreData;
import co.syngleton.chartomancer.core_entities.PurgeOption;
import co.syngleton.chartomancer.data.DataProcessor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class ApiRequestingConfig {
    private CoreData coreData;
    private DataProcessor dataProcessor;
    private ApiRequestingProperties properties;

    @Bean
    CoreData backtestingCoreData() {

        if (!properties.enableRequestingCoreData()) {
            return coreData;
        }

        CoreData backtestingCoreData = DefaultCoreData.copyOf(coreData);

        backtestingCoreData.purgeUselessData(PurgeOption.GRAPHS_AND_PATTERNS);
        dataProcessor.loadGraphs(coreData, properties.dataFolderName(), properties.dataFileNames());
        dataProcessor.createGraphsForMissingTimeframes(coreData);

        return backtestingCoreData;
    }

}

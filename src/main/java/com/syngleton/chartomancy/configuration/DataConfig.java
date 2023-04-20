package com.syngleton.chartomancy.configuration;

import com.syngleton.chartomancy.analytics.ComputationSettings;
import com.syngleton.chartomancy.analytics.ComputationType;
import com.syngleton.chartomancy.data.AppData;
import com.syngleton.chartomancy.factory.PatternSettings;
import com.syngleton.chartomancy.model.Graph;
import com.syngleton.chartomancy.model.Pattern;
import com.syngleton.chartomancy.model.PatternType;
import com.syngleton.chartomancy.service.DataService;
import com.syngleton.chartomancy.service.PatternService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Configuration
public class DataConfig {

    @Value("${data_folder_name}")
    private String dataFolderName;
    @Value("#{'${data_files_names}'.split(',')}")
    private List<String> dataFilesNames;

    private final DataService dataService;
    private final PatternService patternService;

    @Autowired
    public DataConfig(DataService dataService,
                      PatternService patternService) {
        this.dataService = dataService;
        this.patternService = patternService;
    }

    @Bean
    AppData appData() {

        AppData appData = new AppData();
// TODO Externalize initialization methods so they can be tested?
        appData.setGraphs(loadAppData(dataFilesNames));
        appData.setPatternsList(createPatternsForAppData(appData.getGraphs()));
        appData.setPatternsList(computePatternsForAppData(appData.getPatternsList(), appData.getGraphs()));

        return appData;
    }

    private List<Graph> loadAppData(List<String> dataFilesNames) {

        List<Graph> graphs = new ArrayList<>();

        for (String dataFileName : dataFilesNames) {

            Graph graph = dataService.load("./" + dataFolderName + "/" + dataFileName);
            if (graph != null) {
                graphs.add(dataService.load("./" + dataFolderName + "/" + dataFileName));
            }
        }
        if (graphs.isEmpty()) {
            log.error("Application could not initialize its data: no files of correct format could be read.");
        } else {
            log.info("Created graph(s) from files with number: {}", graphs.size());
        }
        return graphs;
    }

    //TODO Discriminate graph types and timeframes to generate multiple sets of graphs?
    private List<List<Pattern>> createPatternsForAppData(List<Graph> graphs) {

        List<List<Pattern>> patternsList = new ArrayList<>();

        if ((graphs != null) && (!graphs.isEmpty())) {
            for (Graph graph : graphs) {
                patternsList.add(patternService.create(new PatternSettings.Builder()
                                .graph(graph)
                                .name("DataConfig")
                                .patternType(PatternType.PREDICTIVE)
                                .autoconfig(PatternSettings.Autoconfig.USE_DEFAULTS)
                        )
                );
            }
        }

        if (patternsList.isEmpty()) {
            log.error("Application could not initialize its data: no patterns could be created.");
        } else {
            log.info("Created {} list(s) of patterns", patternsList.size());
        }
        return patternsList;
    }

    private List<List<Pattern>> computePatternsForAppData(List<List<Pattern>> patternsList, List<Graph> graphs) {

        List<List<Pattern>> computedPatternsList = new ArrayList<>();

        for (List<Pattern> patterns : patternsList) {

            if ((patterns != null) && (!patterns.isEmpty())) {
                computedPatternsList.add(patternService.compute(new ComputationSettings.Builder()
                                .patterns(patterns)
                                .computationType(ComputationType.BASIC_ITERATION)
                                .autoconfig(ComputationSettings.Autoconfig.USE_DEFAULTS)
                                .graph(graphs.get(0))
                        )
                );
            }
        }

        if (computedPatternsList.isEmpty()) {
            log.error("Application could not compute patterns: no pattern has been altered.");
            return patternsList;
        }
        log.info("Computed {} list(s) of patterns", patternsList.size());

        return computedPatternsList;
    }
}

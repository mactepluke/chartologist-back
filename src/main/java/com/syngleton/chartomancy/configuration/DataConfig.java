package com.syngleton.chartomancy.configuration;

import com.syngleton.chartomancy.data.AppData;
import com.syngleton.chartomancy.service.AutomationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Log4j2
@Configuration
public class DataConfig {

    @Value("${data_folder_name}")
    private String dataFolderName;
    @Value("#{'${data_files_names}'.split(',')}")
    private List<String> dataFilesNames;

    private final AutomationService automationService;

    @Autowired
    public DataConfig(AutomationService automationService) {
        this.automationService = automationService;
    }

    @Bean
    AppData appData() {
        return automationService.generateAppData(dataFolderName, dataFilesNames);
    }

}

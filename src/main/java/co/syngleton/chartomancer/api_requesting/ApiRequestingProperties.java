package co.syngleton.chartomancer.api_requesting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties("api-requesting")
public record ApiRequestingProperties(
        @DefaultValue("true") boolean enableRequestingCoreData,
        @DefaultValue("data") String dataFolderName,
        @DefaultValue("[]") List<String> dataFileNames
) {
}

package co.syngleton.chartomancer.security;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(prefix = "web")
public record WebProperties(
        @DefaultValue("http://localhost:4200") List<String> corsAllowedOrigins,
        @DefaultValue("GET,POST,PATCH,PUT,DELETE,OPTIONS,HEAD") List<String> corsAllowedMethods,
        @DefaultValue("Authorization,Content-Type,X-API-Key") List<String> corsAllowedHeaders,
        @DefaultValue("SYCM_API_KEY") String backendApiKey,
        @DefaultValue("CaLkeqVTmwxgXkxuqnQmUsvkLVMldsqPaifRwhJDxkPossNQdUjBFtnYPXGsYjhpA") String jjwtSecret,
        @NotBlank String jjwtExpiration
        ) {
}

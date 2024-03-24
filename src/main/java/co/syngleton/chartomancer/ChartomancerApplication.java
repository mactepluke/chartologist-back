package co.syngleton.chartomancer;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Log4j2
@SpringBootApplication
@EnableFeignClients
@ConfigurationPropertiesScan
public class ChartomancerApplication implements CommandLineRunner {

    @Value("${spring.application.scope}")
    private String appScope;

    public static void main(String[] args) {
        SpringApplication.run(ChartomancerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Application scope: {}", appScope);
    }
}

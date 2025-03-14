package bg.softuni.serviceScheduler.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "scheduler.brands.api")
public class BrandsApiConfig {

    private String baseUrl;

    public BrandsApiConfig setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

}

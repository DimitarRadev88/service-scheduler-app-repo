package bg.softuni.serviceScheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {

    @Bean
    public RestClient genericRestClient() {
        return RestClient.create();
    }

    @Bean("carBrandsRestClient")
    public RestClient carBrandsRestClient(BrandsApiConfig brandsApiConfig) {
        return RestClient.builder()
                .baseUrl(brandsApiConfig.getBaseUrl())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}

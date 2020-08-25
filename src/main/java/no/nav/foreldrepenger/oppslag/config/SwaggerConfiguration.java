package no.nav.foreldrepenger.oppslag.config;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class SwaggerConfiguration {

    @Bean
    public Docket productApi() {
        return new Docket(SWAGGER_2)
                .protocols(Set.of("http", "https"))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}

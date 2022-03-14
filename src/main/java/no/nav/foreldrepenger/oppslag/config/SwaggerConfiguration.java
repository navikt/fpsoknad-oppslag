package no.nav.foreldrepenger.oppslag.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI swaggerOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("Fpsoknad-oppslag")
                .description("Slår opp kontonummer i TPS via WS")
                .version("v0.0.1")
                .license(new License().name("MIT").url("http://nav.no")));
    }
}

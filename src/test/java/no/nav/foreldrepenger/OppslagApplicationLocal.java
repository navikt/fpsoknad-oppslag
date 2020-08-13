package no.nav.foreldrepenger;

import static no.nav.foreldrepenger.boot.conditionals.Cluster.profiler;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

import no.nav.foreldrepenger.oppslag.OppslagApplication;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import no.nav.security.token.support.test.spring.TokenGeneratorConfiguration;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableRetry
@ConfigurationPropertiesScan("no.nav.foreldrepenger.oppslag")
@EnableJwtTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = OppslagApplication.class) })
@EnableOpenApi
@Import(value = TokenGeneratorConfiguration.class)
public class OppslagApplicationLocal {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OppslagApplicationLocal.class)
                .profiles(profiler())
                .run(args);
    }
}

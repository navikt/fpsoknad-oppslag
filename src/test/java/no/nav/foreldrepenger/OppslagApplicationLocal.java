package no.nav.foreldrepenger;

import static no.nav.boot.conditionals.Cluster.profiler;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.retry.annotation.EnableRetry;

import no.nav.foreldrepenger.oppslag.OppslagApplication;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;

@SpringBootApplication
@EnableRetry
@ConfigurationPropertiesScan("no.nav.foreldrepenger.oppslag")
@EnableJwtTokenValidation(ignore = { "org.springframework", "org.springdoc" })
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = OppslagApplication.class) })
public class OppslagApplicationLocal {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OppslagApplicationLocal.class)
                .profiles(profiler())
                .run(args);
    }
}

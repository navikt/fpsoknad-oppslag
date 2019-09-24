package no.nav.foreldrepenger;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;

import no.nav.foreldrepenger.config.ClusterAwareSpringProfileResolver;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import no.nav.security.token.support.test.spring.TokenGeneratorConfiguration;

@SpringBootApplication
@EnableJwtTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = OppslagApplication.class) })

@Import(value = TokenGeneratorConfiguration.class)
public class OppslagApplicationLocal {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OppslagApplicationLocal.class)
                .profiles(new ClusterAwareSpringProfileResolver().getProfile())
                .run(args);
    }
}

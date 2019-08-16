package no.nav.foreldrepenger;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;

import no.nav.foreldrepenger.config.ClusterAwareSpringProfileResolver;
import no.nav.security.oidc.test.support.spring.TokenGeneratorConfiguration;
import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;

@SpringBootApplication
@EnableOIDCTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
@ComponentScan(excludeFilters = { @Filter(type = ASSIGNABLE_TYPE, value = OppslagApplication.class) })

@Import(value = TokenGeneratorConfiguration.class)
public class OppslagApplicationLocal {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OppslagApplicationLocal.class)
                .profiles(new ClusterAwareSpringProfileResolver().getProfile())
                .run(args);
    }
}

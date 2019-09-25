package no.nav.foreldrepenger.oppslag;

import static no.nav.foreldrepenger.oppslag.config.ClusterAwareSpringProfileResolver.profiles;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;

@SpringBootApplication
@EnableCaching
@EnableJwtTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
public class OppslagApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OppslagApplication.class)
                .profiles(profiles())
                .run(args);
    }

}

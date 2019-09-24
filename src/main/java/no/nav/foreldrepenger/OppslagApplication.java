package no.nav.foreldrepenger;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;

import no.nav.foreldrepenger.config.ClusterAwareSpringProfileResolver;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;

@SpringBootApplication
@EnableCaching
@EnableJwtTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
public class OppslagApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OppslagApplication.class)
                .profiles(new ClusterAwareSpringProfileResolver().getProfile())
                .run(args);
    }

}

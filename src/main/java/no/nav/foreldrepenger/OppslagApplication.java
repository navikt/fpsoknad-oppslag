package no.nav.foreldrepenger;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;

import no.nav.foreldrepenger.config.ClusterAwareSpringProfileResolver;
import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;

@SpringBootApplication
@EnableCaching
@EnableOIDCTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
public class OppslagApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OppslagApplication.class)
                .profiles(new ClusterAwareSpringProfileResolver().getProfile())
                .run(args);
    }

}

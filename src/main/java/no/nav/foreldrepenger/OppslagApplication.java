package no.nav.foreldrepenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;

@SpringBootApplication
@EnableCaching
@EnableOIDCTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
public class OppslagApplication {

    public static void main(String[] args) {
        SpringApplication.run(OppslagApplication.class, args);
    }

}

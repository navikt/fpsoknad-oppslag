package no.nav.foreldrepenger.oppslag;

import no.nav.security.spring.oidc.test.TokenGeneratorConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import no.nav.security.spring.oidc.validation.api.EnableOIDCTokenValidation;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableOIDCTokenValidation(ignore = "org.springframework")
@Import(value = TokenGeneratorConfiguration.class)
public class OppslagApplicationLocal {

    public static void main(String[] args) {
        SpringApplication.run(OppslagApplicationLocal.class, args);
    }

}

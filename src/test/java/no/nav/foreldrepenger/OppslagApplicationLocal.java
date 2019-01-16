package no.nav.foreldrepenger;

import static no.nav.foreldrepenger.lookup.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.PREPROD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import no.nav.security.oidc.test.support.spring.TokenGeneratorConfiguration;
import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;

@SpringBootApplication
@EnableOIDCTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
@Import(value = TokenGeneratorConfiguration.class)
public class OppslagApplicationLocal {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OppslagApplicationLocal.class);
        app.setAdditionalProfiles(DEV, PREPROD);
        app.run(args);
    }
}

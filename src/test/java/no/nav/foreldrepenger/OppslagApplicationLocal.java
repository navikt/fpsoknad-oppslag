package no.nav.foreldrepenger;

import static no.nav.foreldrepenger.lookup.util.EnvUtil.DEV;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.LOCAL;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

import no.nav.security.oidc.test.support.spring.TokenGeneratorConfiguration;
import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;

@SpringBootApplication
@EnableOIDCTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
@Import(value = TokenGeneratorConfiguration.class)
public class OppslagApplicationLocal {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OppslagApplicationLocal.class)
                .profiles(DEV, LOCAL)
                .run(args);
    }
}

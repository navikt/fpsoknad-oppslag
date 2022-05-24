package no.nav.foreldrepenger.oppslag.http;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static no.nav.foreldrepenger.oppslag.config.Constants.CLAIMS;
import static no.nav.foreldrepenger.oppslag.config.Constants.TOKENX;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.security.token.support.core.api.ProtectedWithClaims;
import no.nav.security.token.support.core.api.RequiredIssuers;

@RestController
@Documented
@RequiredIssuers(@ProtectedWithClaims(issuer = TOKENX, claimMap = CLAIMS))
@Target(TYPE)
@Retention(RUNTIME)
@RequestMapping
public @interface ProtectedRestController {
    @AliasFor(annotation = RequestMapping.class, attribute = "value")
    String[] value() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "produces")
    String[] produces() default APPLICATION_JSON_VALUE;
}

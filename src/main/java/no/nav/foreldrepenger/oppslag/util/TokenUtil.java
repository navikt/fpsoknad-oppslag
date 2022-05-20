package no.nav.foreldrepenger.oppslag.util;

import static java.time.Instant.now;
import static no.nav.foreldrepenger.oppslag.config.Constants.ISSUER;
import static no.nav.foreldrepenger.oppslag.config.Constants.TOKENX;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.util.DateUtils;

import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.core.jwt.JwtToken;
import no.nav.security.token.support.core.jwt.JwtTokenClaims;

@Component
public class TokenUtil {

    private final TokenValidationContextHolder ctxHolder;

    public TokenUtil(TokenValidationContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public boolean isExpired() {
        return Optional.ofNullable(getExpiryDate())
                .filter(d -> d.before(Date.from(now())))
                .isPresent();
    }

    public boolean erAutentisert() {
        return getSubject() != null;
    }

    public Date getExpiryDate() {
        return Optional.ofNullable(claimSet())
                .map(c -> c.get("exp"))
                .map(this::getDateClaim)
                .orElse(null);
    }

    public String getIssuer() {
        return Optional.ofNullable(claimSet())
                .map(c -> c.get("iss"))
                .map(String.class::cast)
                .orElse(null);
    }

    public String getSubject() {
        return Optional.ofNullable(claimSet())
            .map(this::getSubjectFromPidOrSub)
            .orElse(null);
    }

    private String getSubjectFromPidOrSub(JwtTokenClaims claims) {
        return Optional.ofNullable(claims.getStringClaim("pid"))
            .orElseGet(claims::getSubject);
    }

    public String autentisertBruker() {
        return Optional.ofNullable(getSubject())
                .orElseThrow(unauthenticated("Fant ikke subject"));
    }

    public String getToken() {
        return Stream.of(ISSUER, TOKENX)
                .map(this::getToken)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(unauthenticated("Fant ikke ID-token"));

    }

    private String getToken(String issuer) {
        return Optional.ofNullable(context())
                .map(c -> c.getJwtToken(issuer))
                .filter(Objects::nonNull)
                .map(JwtToken::getTokenAsString)
                .orElse(null);
    }

    private static Supplier<? extends JwtTokenValidatorException> unauthenticated(String msg) {
        return () -> new JwtTokenValidatorException(msg);
    }

    public JwtTokenClaims claimSet() {
        return Stream.of(ISSUER, TOKENX)
                .map(this::claimSet)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private JwtTokenClaims claimSet(String issuer) {
        return Optional.ofNullable(context())
                .map(s -> s.getClaims(issuer))
                .orElse(null);
    }

    private TokenValidationContext context() {
        return Optional.ofNullable(ctxHolder.getTokenValidationContext())
                .orElse(null);
    }

    private Date getDateClaim(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof Number) {
            return DateUtils.fromSecondsSinceEpoch(((Number) value).longValue());
        }
        return null;

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ctxHolder=" + ctxHolder + "]";
    }

}

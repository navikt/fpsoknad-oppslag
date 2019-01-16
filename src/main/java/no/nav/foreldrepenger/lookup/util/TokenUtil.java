package no.nav.foreldrepenger.lookup.util;

import static no.nav.foreldrepenger.lookup.Constants.ISSUER;
import static no.nav.foreldrepenger.lookup.util.StreamUtil.not;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;

import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.context.TokenContext;
import no.nav.security.oidc.exceptions.OIDCTokenValidatorException;

@Component
public class TokenUtil {

    private final OIDCRequestContextHolder ctxHolder;

    public TokenUtil(OIDCRequestContextHolder ctxHolder) {
        this.ctxHolder = ctxHolder;
    }

    public boolean erAutentisert() {
        return getSubject() != null;
    }

    public boolean isExpired() {
        Date exp = getExp();
        return exp == null ? true : exp.before(new Date());
    }

    public Date getExp() {
        return Optional.ofNullable(claimSet())
                .map(JWTClaimsSet::getExpirationTime)
                .orElse(null);
    }

    public Fødselsnummer getSubject() {
        return Optional.ofNullable(claimSet())
                .map(JWTClaimsSet::getSubject)
                .map(Fødselsnummer::new)
                .orElse(null);
    }

    public Fødselsnummer autentisertBruker() {
        return Optional.ofNullable(getSubject())
                .orElseThrow(unauthenticated("Fant ikke subject"));
    }

    private static Supplier<? extends OIDCTokenValidatorException> unauthenticated(String msg) {
        return () -> new OIDCTokenValidatorException(msg);
    }

    private JWTClaimsSet claimSet() {
        return Optional.ofNullable(claims())
                .map(OIDCClaims::getClaimSet)
                .orElse(null);
    }

    private OIDCClaims claims() {
        return Optional.ofNullable(context())
                .map(s -> s.getClaims(ISSUER))
                .orElse(null);
    }

    private OIDCValidationContext context() {
        return Optional.ofNullable(ctxHolder.getOIDCValidationContext())
                .orElse(null);
    }

    public String getToken() {
        return Optional.ofNullable(context())
                .map(c -> c.getToken(ISSUER))
                .filter(not(Objects::isNull))
                .map(TokenContext::getIdToken)
                .orElseThrow(unauthenticated("Fant ikke ID-token"));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [ctxHolder=" + ctxHolder + "]";
    }

}

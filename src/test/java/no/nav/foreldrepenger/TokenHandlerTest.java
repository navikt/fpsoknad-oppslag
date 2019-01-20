package no.nav.foreldrepenger;

import static no.nav.foreldrepenger.lookup.Constants.ISSUER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.nimbusds.jwt.JWTClaimsSet;

import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.security.oidc.context.OIDCClaims;
import no.nav.security.oidc.context.OIDCRequestContextHolder;
import no.nav.security.oidc.context.OIDCValidationContext;
import no.nav.security.oidc.context.TokenContext;
import no.nav.security.oidc.exceptions.OIDCTokenValidatorException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
public class TokenHandlerTest {

    private static final Fødselsnummer FNR = new Fødselsnummer("42");
    @Mock
    private OIDCRequestContextHolder holder;
    @Mock
    private OIDCValidationContext context;
    @Mock
    private OIDCClaims claims;
    @Mock
    private TokenContext tokenContext;

    private TokenUtil tokenHandler;

    @BeforeEach
    public void before() {
        when(holder.getOIDCValidationContext()).thenReturn(context);
        when(context.getClaims(eq(ISSUER))).thenReturn(claims);
        tokenHandler = new TokenUtil(holder);
    }

    @Test
    public void testTokenExpiry() {
        when(claims.getClaimSet()).thenReturn(new JWTClaimsSet.Builder()
                .subject(FNR.getFnr())
                .expirationTime(toDate(LocalDateTime.now().plusHours(1)))
                .build());
        assertNotNull(tokenHandler.getExp());

    }

    private static Date toDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    public void testOK() {
        when(claims.getClaimSet()).thenReturn(new JWTClaimsSet.Builder().subject(FNR.getFnr()).build());
        assertEquals(FNR, tokenHandler.autentisertBruker());
        assertEquals(FNR, tokenHandler.getSubject());
        assertTrue(tokenHandler.erAutentisert());
    }

    @Test
    public void testNoContext() {
        when(holder.getOIDCValidationContext()).thenReturn(null);
        assertFalse(tokenHandler.erAutentisert());
        assertNull(tokenHandler.getSubject());
        assertNull(tokenHandler.getExp());
        assertThrows(OIDCTokenValidatorException.class, () -> tokenHandler.autentisertBruker());
    }

    @Test
    public void testNoClaims() {
        when(context.getClaims(eq(ISSUER))).thenReturn(null);
        assertFalse(tokenHandler.erAutentisert());
        assertNull(tokenHandler.getSubject());
        assertThrows(OIDCTokenValidatorException.class, () -> tokenHandler.autentisertBruker());
    }

    @Test
    public void testNoClaimset() {
        assertNull(tokenHandler.getSubject());
        assertFalse(tokenHandler.erAutentisert());
        assertThrows(OIDCTokenValidatorException.class, () -> tokenHandler.autentisertBruker());
    }

    @Test
    public void testNoToken() {
        when(context.getToken(eq(ISSUER))).thenReturn(null);
        assertThrows(OIDCTokenValidatorException.class, () -> tokenHandler.getToken());
    }

    @Test
    public void testNoSubject() {
        when(claims.getClaimSet()).thenReturn(new JWTClaimsSet.Builder().build());
        assertNull(tokenHandler.getSubject());
        assertFalse(tokenHandler.erAutentisert());
        assertThrows(OIDCTokenValidatorException.class, () -> tokenHandler.autentisertBruker());
    }
}

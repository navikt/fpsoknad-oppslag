package no.nav.foreldrepenger.oppslag.util;

import static no.nav.foreldrepenger.oppslag.config.Constants.ISSUER;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.exceptions.JwtTokenValidatorException;
import no.nav.security.token.support.core.jwt.JwtTokenClaims;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
public class TokenUtilTest {

    private static final Fødselsnummer FNR = new Fødselsnummer("42");
    @Mock
    private TokenValidationContextHolder holder;
    @Mock
    private TokenValidationContext context;
    @Mock
    private JwtTokenClaims claims;

    private TokenUtil tokenHandler;

    @BeforeEach
    public void before() {
        when(holder.getTokenValidationContext()).thenReturn(context);
        when(context.getClaims(eq(ISSUER))).thenReturn(claims);
        tokenHandler = new TokenUtil(holder);
    }

    @Test
    public void testTokenExpiry() {
        when(claims.get(eq("exp")))
                .thenReturn(toDate(LocalDateTime.now().minusHours(1)).toInstant().getEpochSecond());
        assertNotNull(tokenHandler.getExpiryDate());
        assertTrue(tokenHandler.isExpired());
    }

    private static Date toDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    public void testOK() {
        when(claims.get(eq("exp")))
                .thenReturn(toDate(LocalDateTime.now().minusHours(1)).toInstant().getEpochSecond());
        when(claims.getSubject()).thenReturn(FNR.getFnr());
        assertEquals(FNR.getFnr(), tokenHandler.autentisertBruker());
        assertEquals(FNR.getFnr(), tokenHandler.getSubject());
        assertTrue(tokenHandler.erAutentisert());
    }

    @Test
    public void testNoContext() {
        when(holder.getTokenValidationContext()).thenReturn(null);
        assertFalse(tokenHandler.erAutentisert());
        assertNull(tokenHandler.getSubject());
        assertNull(tokenHandler.getExpiryDate());
        assertThrows(JwtTokenValidatorException.class, () -> tokenHandler.autentisertBruker());
    }

    @Test
    public void testNoClaims() {
        when(context.getClaims(eq(ISSUER))).thenReturn(null);
        assertFalse(tokenHandler.erAutentisert());
        assertNull(tokenHandler.getSubject());
        assertThrows(JwtTokenValidatorException.class, () -> tokenHandler.autentisertBruker());
    }

    @Test
    public void testNoClaimset() {
        assertNull(tokenHandler.getSubject());
        assertFalse(tokenHandler.erAutentisert());
        assertThrows(JwtTokenValidatorException.class, () -> tokenHandler.autentisertBruker());
    }

    @Test
    public void testNoToken() {
        when(context.getJwtToken(eq(ISSUER))).thenReturn(null);
        assertThrows(JwtTokenValidatorException.class, () -> tokenHandler.getToken());
    }

    @Test
    public void testNoSubject() {
        assertNull(tokenHandler.getSubject());
        assertFalse(tokenHandler.erAutentisert());
        assertThrows(JwtTokenValidatorException.class, () -> tokenHandler.autentisertBruker());
    }
}

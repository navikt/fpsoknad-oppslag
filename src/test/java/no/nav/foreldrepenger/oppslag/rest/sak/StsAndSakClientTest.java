package no.nav.foreldrepenger.oppslag.rest.sak;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.oppslag.config.Constants;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørId;
import no.nav.security.token.support.test.JwtTokenGenerator;

@EnableRetry
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
@ContextConfiguration(classes = { TokenUtil.class, SakConfiguration.class })
@TestPropertySource(properties = {
        "sak.securitytokenservice.url=http://sts", "sak.saker.url=http://sak", "sak.securitytokenservice.password=mypw",
        "sak.securitytokenservice.username=myuser" })
public class StsAndSakClientTest {

    private static final String ID = "222222222";
    private static final AktørId AKTOR = new AktørId(ID);
    private static final String SIGNED_JWT = JwtTokenGenerator.createSignedJWT("22222222222").serialize();
    private static final String MY_OIDC_TOKEN = "MY.OIDC.TOKEN";
    private static final String MYPW = "mypw";
    private static final String MYUSER = "myuser";
    private static final String ASSERTION = "<saml2:Assertion .......... </saml2:Assertion>";
    private static final String ENVELOPE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<soapenv:Envelope  ..." + ASSERTION + "</wst:blabla</soapenv:Envelope>";
    private static final URI SAKURL = URI.create("http://sak?aktoerId=" + ID + "&applikasjon=IT01&tema=FOR");
    private static final URI STSURL = URI.create("http://sts");

    @MockBean
    private RestOperations restOperations;
    @MockBean
    private TokenUtil tokenHandler;
    @Autowired
    private SakClient sakclient;
    @Autowired
    private StsClient stsclient;

    @BeforeEach
    public void beforeEach() {
        when(tokenHandler.getToken()).thenReturn(SIGNED_JWT);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSakAndSTSRetryRecovery() {
        whenSak()
                .thenThrow(internalServerError())
                .thenReturn(remoteSaker());
        whenSTS().thenThrow(internalServerError())
                .thenReturn(ENVELOPE);
        assertEquals(sakclient.sakerFor(AKTOR, Constants.FORELDREPENGER).size(), 1);
        verifySak(2);
        verifySTS(3);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSTSogSakOK() {
        whenSak().thenReturn(remoteSaker());
        whenSTS().thenReturn(ENVELOPE);
        assertEquals(sakclient.sakerFor(AKTOR, Constants.FORELDREPENGER).size(), 1);
        verifySak(1);
        verifySTS(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSakRetryUntilFail() {
        whenSTS().thenReturn(ENVELOPE);
        whenSak().thenThrow(internalServerError());
        assertThrows(HttpServerErrorException.class, () -> sakclient.sakerFor(AKTOR, Constants.FORELDREPENGER));
        verifySak(2);
        verifySTS(2);
    }

    @Test
    public void testSTSRetryUntilFail() {
        whenSTS().thenThrow(internalServerError());
        assertThrows(HttpServerErrorException.class, () -> stsclient.oidcToSamlToken("test"));
        verifySTS(2);
    }

    @Test
    public void testInject() {
        String payload = stsclient.injectToken(MY_OIDC_TOKEN);
        assertTrue(payload.startsWith("<?xml"));
        assertTrue(payload.contains("<wsse:Username>" + MYUSER +
                "</wsse:Username>"));
        assertTrue(payload.contains(
                "<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">"
                        + MYPW + "</wsse:Password>"));
        assertTrue(payload.contains(MY_OIDC_TOKEN));
    }

    @Test
    public void testExtraction() {
        assertEquals(ASSERTION,
                StsClientHttp.samlAssertionFra(ENVELOPE));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private OngoingStubbing whenSak() {
        return when(restOperations.exchange(eq(SAKURL), eq(GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)));
    }

    @SuppressWarnings("rawtypes")
    private OngoingStubbing whenSTS() {
        return when(restOperations.postForObject(eq(STSURL), any(HttpEntity.class), eq(String.class)));
    }

    private static HttpServerErrorException internalServerError() {
        return new HttpServerErrorException(INTERNAL_SERVER_ERROR);
    }

    private static ResponseEntity<List<RemoteSak>> remoteSaker() {
        return new ResponseEntity<>(Collections.singletonList(
                new RemoteSak(42L, "FOR", "IT01", "42", ID, "42", "Donald Duck", LocalDateTime.now().toString())), OK);
    }

    private String verifySTS(int n) {
        return verify(restOperations, times(n)).postForObject(eq(STSURL),
                any(HttpEntity.class), eq(String.class));
    }

    @SuppressWarnings("unchecked")
    private void verifySak(int n) {
        verify(restOperations, times(n)).exchange(eq(SAKURL), eq(GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }
}

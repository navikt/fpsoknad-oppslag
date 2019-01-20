package no.nav.foreldrepenger.lookup.rest.sak;

import static no.nav.foreldrepenger.lookup.ws.WSTestUtil.retriedOK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.security.oidc.test.support.JwtTokenGenerator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
public class StsAndSakClientTest {

    private static final String ID = "222222222";
    private static final AktorId AKTOR = new AktorId(ID);
    private static final String SIGNED_JWT = JwtTokenGenerator.createSignedJWT("22222222222").serialize();
    private static final String MY_OIDC_TOKEN = "MY.OIDC.TOKEN";
    private static final String MYPW = "mypw";
    private static final String MYUSER = "myuser";
    private static final String ASSERTION = "<saml2:Assertion .......... </saml2:Assertion>";
    private static final String ENVELOPE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<soapenv:Envelope  ..." + ASSERTION + "</wst:blabla</soapenv:Envelope>";
    private static final URI SAKURL = URI.create("http://sak");
    private static final URI STSURL = URI.create("http://sts");

    @Mock
    private RestOperations restOperations;
    @Mock
    private TokenUtil tokenHandler;
    private StsClient stsclient;
    private SakClient sakclient;

    @BeforeEach
    public void beforeEach() {
        stsclient = new StsClient(restOperations, STSURL, MYUSER, MYPW);
        sakclient = new SakClientHttp(SAKURL, restOperations, stsclient, tokenHandler);
        when(tokenHandler.getToken()).thenReturn(SIGNED_JWT);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSakAndSTSRetryRecovery() {
        when(restOperations.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                        .thenThrow(internalServerError())
                        .thenReturn(remoteSaker());
        when(restOperations.postForObject(eq(STSURL), any(HttpEntity.class), eq(String.class)))
                .thenThrow(internalServerError())
                .thenReturn(ENVELOPE);
        assertEquals(sakclient.sakerFor(AKTOR).size(), 1);
        verify(restOperations, times(2)).exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
        verify(restOperations, retriedOK()).postForObject(eq(STSURL), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testVanilla() {
        when(restOperations.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                        .thenReturn(remoteSaker());
        when(restOperations.postForObject(eq(STSURL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ENVELOPE);
        assertEquals(sakclient.sakerFor(AKTOR).size(), 1);
        verify(restOperations).exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
        verify(restOperations).postForObject(eq(STSURL), any(HttpEntity.class), eq(String.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSakSTSRetryUntilFail() {
        when(restOperations.postForObject(eq(STSURL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ENVELOPE);
        when(restOperations.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class)))
                        .thenThrow(internalServerError());
        assertThrows(HttpServerErrorException.class, () -> sakclient.sakerFor(AKTOR));
        verify(restOperations, times(2)).exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    public void testSTSRetryUntilFail() {
        when(restOperations.postForObject(eq(STSURL), any(HttpEntity.class), eq(String.class)))
                .thenThrow(internalServerError());
        assertThrows(HttpStatusCodeException.class, () -> {
            stsclient.oidcToSamlToken(MY_OIDC_TOKEN);
        });
        verify(restOperations, retriedOK()).postForObject(eq(STSURL), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testSTSRecovery() {
        when(restOperations.postForObject(eq(STSURL), any(HttpEntity.class), eq(String.class)))
                .thenThrow(internalServerError())
                .thenReturn(ENVELOPE);
        stsclient.oidcToSamlToken(MY_OIDC_TOKEN);
        verify(restOperations, retriedOK()).postForObject(eq(STSURL), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testInject() {
        String payload = stsclient.injectToken(MY_OIDC_TOKEN);
        assertTrue(payload.startsWith("<?xml"));
        assertTrue(payload.contains("<wsse:Username>" + MYUSER + "</wsse:Username>"));
        assertTrue(payload.contains(
                "<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">"
                        + MYPW + "</wsse:Password>"));
        assertTrue(payload.contains(MY_OIDC_TOKEN));
    }

    @Test
    public void testExtraction() {
        assertEquals(ASSERTION, StsClient.samlAssertionFra(ENVELOPE));
    }

    private static HttpServerErrorException internalServerError() {
        return new HttpServerErrorException(INTERNAL_SERVER_ERROR);
    }

    private static ResponseEntity<List<RemoteSak>> remoteSaker() {
        return new ResponseEntity<>(Collections.singletonList(
                new RemoteSak(42, "FOR", "IT01", "42", ID, "42", "Donald Duck", LocalDateTime.now().toString())), OK);
    }
}

package no.nav.foreldrepenger.lookup.rest.sak;

import static io.github.resilience4j.retry.Retry.decorateSupplier;
import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static no.nav.foreldrepenger.lookup.util.RetryUtil.DEFAULT_RETRIES;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestOperations;

import io.github.resilience4j.retry.Retry;
import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.lookup.TokenHandler;
import no.nav.foreldrepenger.lookup.util.RetryUtil;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;

public class SakClientHttp implements SakClient {

    private static final Logger LOG = LoggerFactory.getLogger(SakClientHttp.class);

    private final RestOperations restOperations;
    private final String sakBaseUrl;
    private final StsClient stsClient;
    private final TokenHandler tokenHandler;
    private final Retry retry;

    public SakClientHttp(String sakBaseUrl, RestOperations restOperations, StsClient stsClient,
            TokenHandler tokenHandler) {
        this(sakBaseUrl, restOperations, stsClient, tokenHandler, retry());
    }

    public SakClientHttp(String sakBaseUrl, RestOperations restOperations, StsClient stsClient,
            TokenHandler tokenHandler, Retry retry) {
        this.restOperations = restOperations;
        this.sakBaseUrl = sakBaseUrl;
        this.stsClient = stsClient;
        this.tokenHandler = tokenHandler;
        this.retry = retry;
    }

    @Override
    @Timed("lookup.sak")
    public List<Sak> sakerFor(AktorId aktor) {
        ResponseEntity<List<RemoteSak>> response = sakerFor(aktor.getAktør(), request());
        return sisteSakFra(Optional.ofNullable(response.getBody()).orElse(emptyList()));
    }

    private HttpEntity<String> request() {
        return new HttpEntity<>("", headers(stsClient.oidcToSamlToken(tokenHandler.getToken())));
    }

    private static List<Sak> sisteSakFra(List<RemoteSak> saker) {
        LOG.info("Fant {} sak(er)", saker.size());
        if (!saker.isEmpty()) {
            LOG.trace("{}", saker);
        }
        Sak sisteSak = saker.stream()
                .map(RemoteSakMapper::map)
                .filter(s -> s.getOpprettet() != null)
                .filter(s -> s.getOpprettet().isAfter(now().minusYears(3)))
                .max(comparing(Sak::getOpprettet))
                .orElse(null);
        return sisteSak != null ? singletonList(sisteSak) : emptyList();
    }

    private ResponseEntity<List<RemoteSak>> sakerFor(String aktor, HttpEntity<String> request) {
        return decorateSupplier(retry, () -> {
            String url = sakBaseUrl + "?aktoerId=" + aktor + "&applikasjon=IT01&tema=FOR";
            LOG.trace("henter saker fra {}", url);
            return restOperations.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<List<RemoteSak>>() {
                    });
        }).get();
    }

    private static HttpHeaders headers(String samlToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, "Saml " + encode(samlToken));
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        return headers;
    }

    private static String encode(String samlToken) {
        try {
            return Base64.getEncoder().encodeToString(samlToken.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Retry retry() {
        return RetryUtil.retry(DEFAULT_RETRIES, "saker", HttpServerErrorException.class, LOG);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [restOperations=" + restOperations + ", sakBaseUrl=" + sakBaseUrl
                + ", stsClient=" + stsClient + ", tokenHandler=" + tokenHandler + ", retry=" + retry + "]";
    }

}

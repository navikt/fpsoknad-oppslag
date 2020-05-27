package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.rest;

import static java.util.Collections.singletonList;
import static no.nav.foreldrepenger.oppslag.util.StringUtil.encode;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.oppslag.rest.filters.SystemUserTokenService;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;

public class ArbeidsforholdClientRest implements ArbeidsforholdTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdClientRest.class);
    private final RestOperations restOperations;
    private final URI baseUri;
    private final SystemUserTokenService tokenService;
    private final TokenUtil tokenUtil;

    public ArbeidsforholdClientRest(URI baseUri, RestOperations restOperations, SystemUserTokenService tokenService,
            TokenUtil tokenUtil) {
        this.restOperations = restOperations;
        this.baseUri = baseUri;
        this.tokenService = tokenService;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void ping() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Arbeidsforhold> aktiveArbeidsforhold() {
        return aktiveArbeidsforhold(new Fødselsnummer(tokenUtil.autentisertBruker()));
    }

    @Override
    public List<Arbeidsforhold> aktiveArbeidsforhold(Fødselsnummer fnr) {
        LOG.info("Henter arbeidsforhold for {}", fnr);
        var response = arbeidsforholdFor(request());
        return Collections.emptyList();

    }

    private List<Arbeidsforhold> arbeidsforholdFor(HttpEntity<String> request) {
        return Collections.emptyList();
    }

    @Override
    public String arbeidsgiverNavn(String orgnr) {
        return "TODO";
    }

    private HttpEntity<String> request() {
        return new HttpEntity<>(headers(tokenService.fetch().getAccessToken()));
    }

    private HttpHeaders headers(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Nav-Consumer-Token", "Bearer " + encode(jwtToken));
        headers.set(AUTHORIZATION, "Bearer " + tokenUtil.getToken());
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        return headers;
    }
}

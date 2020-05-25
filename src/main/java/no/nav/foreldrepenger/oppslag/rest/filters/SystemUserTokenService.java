package no.nav.foreldrepenger.oppslag.rest.filters;

import java.net.URI;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SystemUserTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemUserTokenService.class);

    private final RestTemplate restTemplate;

    private final String username;

    private final String password;

    private final URI uri;

    public SystemUserTokenService(RestTemplate restTemplate, @Value("${FPSELVBETJENING_USERNAME}") String username,
            @Value("${FPSELVBETJENING_PASSWORD}") String password,
            @Value("${sts.uri}") URI uri) {
        this.restTemplate = restTemplate;
        this.username = username;
        this.password = password;
        this.uri = uri;
        LOG.info("Token er {}", fetch());
    }

    public UserToken fetch() {

        LOG.trace("Retrieving JWT token for service user");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION,
                getBasicAuthHeader());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> restRequest = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(uri, HttpMethod.GET,
                    restRequest, UserToken.class).getBody();
        } catch (Exception e) {
            LOG.warn("Retrieving JWT token for service user feilet", e);
            return null;
        }

    }

    private String getBasicAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

}

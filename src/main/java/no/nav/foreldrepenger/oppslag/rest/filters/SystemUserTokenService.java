package no.nav.foreldrepenger.oppslag.rest.filters;

import java.net.URI;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
        LOG.info("STS uri er" + uri);
    }

    /*
     * public UserToken fetch() { return null;
     * 
     * LOGGER.trace("Retrieving JWT token for service user"); HttpHeaders headers =
     * new HttpHeaders(); headers.add(HttpHeaders.AUTHORIZATION,
     * getBasicAuthHeader()); headers.setContentType(MediaType.APPLICATION_JSON);
     * 
     * HttpEntity restRequest = new HttpEntity<>(headers);
     * 
     * UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(uri)
     * .path("/rest/v1/sts/token").queryParam("grant_type", "client_credentials")
     * .queryParam("scope", "openid");
     * 
     * try { return restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET,
     * restRequest, UserToken.class).getBody(); } catch (HttpStatusCodeException e)
     * { throw new RuntimeException("Feil ved henting av SystembrukerToken: " +
     * e.getMessage(), e); }
     * 
     * }
     */

    private String getBasicAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

}

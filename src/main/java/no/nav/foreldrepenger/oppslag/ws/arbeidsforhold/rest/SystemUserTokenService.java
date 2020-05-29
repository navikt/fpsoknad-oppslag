package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SystemUserTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemUserTokenService.class);

    private final WebClient webClient;

    public SystemUserTokenService(WebClient webClient) {
        this.webClient = webClient;
        // fetch();
    }

    public UserToken fetch() {
        LOG.trace("Henter JWT token for service user");
        var token = webClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserToken.class)
                .block();
        LOG.trace("Hentet JWT token for service user {}", token);
        return token;

    }

}

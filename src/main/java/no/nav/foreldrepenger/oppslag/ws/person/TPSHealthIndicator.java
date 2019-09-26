package no.nav.foreldrepenger.oppslag.ws.person;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.rest.PingableHealthIndicator;

@Component
public class TPSHealthIndicator extends PingableHealthIndicator {

    private final PersonTjeneste client;

    public TPSHealthIndicator(PersonTjeneste client, @Value("${VIRKSOMHET_PERSON_V3_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}

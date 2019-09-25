package no.nav.foreldrepenger.oppslag.ws.person;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.rest.EnvironmentAwareServiceHealthIndicator;

@Component
public class TPSHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final PersonClient client;

    public TPSHealthIndicator(PersonClient client, @Value("${VIRKSOMHET_PERSON_V3_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}

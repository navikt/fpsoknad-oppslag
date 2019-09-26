package no.nav.foreldrepenger.oppslag.ws.aktor;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.rest.PingableHealthIndicator;

@Component
public class AktørHealthIndicator extends PingableHealthIndicator {

    private final AktørTjeneste client;

    public AktørHealthIndicator(AktørTjeneste client, @Value("${AKTOER_V2_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    public void ping() {
        client.ping();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[client=" + client + "]";
    }
}

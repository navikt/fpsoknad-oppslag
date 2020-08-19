package no.nav.foreldrepenger.oppslag.ws.aktor;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.health.PingableHealthIndicator;

@Component
public class AktørHealthIndicator extends PingableHealthIndicator {

    private final AktørTjeneste client;

    public AktørHealthIndicator(AktørTjeneste client, @Value("${aktoer.v2.endpointurl}") URI serviceUrl) {
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

package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.rest.PingableHealthIndicator;

@Component
public class AaregHealthIndicator extends PingableHealthIndicator {

    private final ArbeidsforholdTjeneste client;

    public AaregHealthIndicator(ArbeidsforholdTjeneste client,
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") URI serviceUrl) {
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

package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.rest.EnvironmentAwareServiceHealthIndicator;

@Component
public class AaregHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final ArbeidsforholdTjeneste client;

    public AaregHealthIndicator(ArbeidsforholdTjeneste client,
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") URI serviceUrl) {
        super(serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}

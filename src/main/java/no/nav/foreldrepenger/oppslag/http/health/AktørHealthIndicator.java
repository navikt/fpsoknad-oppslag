package no.nav.foreldrepenger.oppslag.http.health;

import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class AktørHealthIndicator extends EnvironmentAwareServiceHealthIndicator {

    private final AktorIdClient client;

    public AktørHealthIndicator(AktorIdClient client, Environment env,
                                @Value("${AKTOER_V2_ENDPOINTURL}") URI serviceUrl) {
        super(env, serviceUrl);
        this.client = client;
    }

    @Override
    protected void checkHealth() {
        client.ping();
    }
}

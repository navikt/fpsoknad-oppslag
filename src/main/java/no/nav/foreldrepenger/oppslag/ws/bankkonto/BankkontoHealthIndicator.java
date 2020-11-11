package no.nav.foreldrepenger.oppslag.ws.bankkonto;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.rest.PingableHealthIndicator;

@Component
public class BankkontoHealthIndicator extends PingableHealthIndicator {

    private final BankkontoTjeneste client;

    public BankkontoHealthIndicator(BankkontoTjeneste client, @Value("${virksomhet.person.v3.endpointurl}") URI serviceUrl) {
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

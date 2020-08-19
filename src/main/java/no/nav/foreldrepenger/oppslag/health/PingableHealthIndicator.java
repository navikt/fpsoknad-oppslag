package no.nav.foreldrepenger.oppslag.health;

import java.net.URI;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import no.nav.foreldrepenger.oppslag.util.Pingable;

public abstract class PingableHealthIndicator implements HealthIndicator, Pingable {

    private final URI serviceUrl;

    public PingableHealthIndicator(URI serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public Health health() {
        try {
            ping();
            return Health.up()
                    .withDetail("url", serviceUrl)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("url", serviceUrl)
                    .withException(e).build();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [url=" + serviceUrl + "]";
    }
}

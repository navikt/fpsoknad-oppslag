package no.nav.foreldrepenger.oppslag.rest;

import java.net.URI;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public abstract class PingableHealthIndicator implements HealthIndicator {

    private final URI serviceUrl;

    protected abstract void checkHealth();

    public PingableHealthIndicator(URI serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public Health health() {
        try {
            checkHealth();
            return upWithDetails();
        } catch (Exception e) {
            return downWithDetails(e);
        }
    }

    private Health downWithDetails(Exception e) {
        return Health.down().withDetail("url", serviceUrl).withException(e).build();
    }

    private Health upWithDetails() {
        return Health.up().withDetail("url", serviceUrl).build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [url=" + serviceUrl + "]";
    }
}

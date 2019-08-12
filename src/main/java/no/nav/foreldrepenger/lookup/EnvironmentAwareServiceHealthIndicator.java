package no.nav.foreldrepenger.lookup;

import static no.nav.foreldrepenger.lookup.util.EnvUtil.isDevOrLocal;

import java.net.URI;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public abstract class EnvironmentAwareServiceHealthIndicator implements EnvironmentAware, HealthIndicator {

    private final URI serviceUrl;
    private Environment env;

    protected abstract void checkHealth();

    public EnvironmentAwareServiceHealthIndicator(URI serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public Health health() {
        try {
            checkHealth();
            return isDevOrLocal(env) ? upWithDetails() : up();
        } catch (Exception e) {
            return isDevOrLocal(env) ? downWithDetails(e) : down();
        }
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    private static Health down() {
        return Health.down().build();
    }

    private Health downWithDetails(Exception e) {
        return Health.down().withDetail("url", serviceUrl).withException(e).build();
    }

    private static Health up() {
        return Health.up().build();
    }

    private Health upWithDetails() {
        return Health.up().withDetail("url", serviceUrl).build();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [url=" + serviceUrl + "]";
    }
}

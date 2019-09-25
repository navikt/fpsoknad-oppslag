package no.nav.foreldrepenger.oppslag.util;

import static io.vavr.API.$;

import org.slf4j.Logger;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.API;
import io.vavr.Predicates;

public final class RetryUtil {

    public static final int DEFAULT_RETRIES = 2;

    private RetryUtil() {

    }

    public static Retry retry(int max, String name, Class<? extends Throwable> clazz, Logger LOG) {
        Retry retry = RetryRegistry.of(RetryConfig.custom()
                .retryOnException(throwable -> API.Match(throwable).of(
                        API.Case($(Predicates.instanceOf(clazz)), true),
                        API.Case($(), false)))
                .maxAttempts(max)
                .build()).retry(name);
        retry.getEventPublisher()
                .onRetry(event -> LOG.warn("Prøver {} igjen for {}. gang av {} grunnet {} ({})",
                        name,
                        event.getNumberOfRetryAttempts(),
                        max,
                        event.getLastThrowable().getClass().getSimpleName(),
                        event.getLastThrowable().getMessage()))
                .onSuccess(event -> LOG.info("Hentet fra {} på {}. forsøk av {}",
                        name,
                        event.getNumberOfRetryAttempts(),
                        max))
                .onError(event -> LOG.warn("Kunne ikke hente {} etter {} forsøk",
                        name,
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable()));
        return retry;
    }
}

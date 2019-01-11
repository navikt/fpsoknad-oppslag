package no.nav.foreldrepenger.lookup.util;

import static io.vavr.API.$;

import org.slf4j.Logger;
import org.springframework.remoting.soap.SoapFaultException;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.API;
import io.vavr.Predicates;

public class RetryUtil {

    private RetryUtil() {

    }

    public static Retry retry(String name, Logger LOG) {
        return retry(name, LOG, 2);
    }

    public static Retry retry(String name, Logger LOG, int attempts) {
        return retry(name, LOG, attempts, SoapFaultException.class);
    }

    public static Retry retry(String name, Logger LOG, int attempts, Class<?> clazz) {
        Retry retry = RetryRegistry.of(RetryConfig.custom()
                .retryOnException(throwable -> API.Match(throwable).of(
                        API.Case($(Predicates.instanceOf(clazz)), true),
                        API.Case($(), false)))
                .maxAttempts(attempts)
                .build()).retry(name);
        retry.getEventPublisher()
                .onRetry(event -> LOG.info("Prøver igjen for {}. gang av {} grunnet {}", retry.getRetryConfig(),
                        event.getNumberOfRetryAttempts(),
                        retry.getRetryConfig().getMaxAttempts(), event.getLastThrowable().getClass().getSimpleName()))
                .onSuccess(event -> LOG.info("Hentet person OK på {}. forsøk", event.getNumberOfRetryAttempts()))
                .onError(event -> LOG.warn("Kunne ikke eksekvere {} etter {} forsøk", name,
                        event.getNumberOfRetryAttempts(), event.getLastThrowable()));
        return retry;
    }
}

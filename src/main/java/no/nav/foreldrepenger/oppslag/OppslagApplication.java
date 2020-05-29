package no.nav.foreldrepenger.oppslag;

import static java.util.Collections.singletonList;
import static no.nav.foreldrepenger.oppslag.config.ClusterAwareSpringProfileResolver.profiles;
import static org.springframework.retry.RetryContext.NAME;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.util.ReflectionUtils;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;

@SpringBootApplication
@ConfigurationPropertiesScan("no.nav.foreldrepenger.oppslag")
@EnableCaching
@EnableRetry
@EnableAutoConfiguration(exclude = WebFluxAutoConfiguration.class)
@EnableJwtTokenValidation(ignore = { "org.springframework", "springfox.documentation" })
public class OppslagApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(OppslagApplication.class)
                .profiles(profiles())
                .run(args);
    }

    @Bean
    public List<RetryListener> retryListeners() {
        Logger log = LoggerFactory.getLogger(getClass());

        return singletonList(new RetryListener() {

            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
                    Throwable throwable) {
                log.warn("Metode {} kastet exception {} for {}. gang",
                        context.getAttribute(NAME), throwable.toString(), context.getRetryCount());
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext ctx, RetryCallback<T, E> callback,
                    Throwable t) {
                if (t != null) {
                    log.warn("Metode {} avslutter ikke-vellykket retry etter {}. forsøk",
                            ctx.getAttribute(NAME), ctx.getRetryCount(), t);
                } else {
                    if (ctx.getRetryCount() > 0) {
                        log.info("Metode {} avslutter vellykket retry etter {}. forsøk",
                                ctx.getAttribute(NAME), ctx.getRetryCount());
                    } else {
                        log.trace("Metode {} avslutter vellykket uten retry", ctx.getAttribute(NAME));
                    }
                }
            }

            @Override
            public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                Field labelField = ReflectionUtils.findField(callback.getClass(), "val$label");
                ReflectionUtils.makeAccessible(labelField);
                String metode = (String) ReflectionUtils.getField(labelField, callback);
                if (context.getRetryCount() > 0) {
                    log.info("Metode {} gjør retry for {}. gang", metode, context.getRetryCount());
                } else {
                    log.trace("Metode {} initierer retry", metode);
                }
                return true;
            }
        });
    }

}

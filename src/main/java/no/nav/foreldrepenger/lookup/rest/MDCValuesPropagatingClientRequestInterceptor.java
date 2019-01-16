package no.nav.foreldrepenger.lookup.rest;

import static no.nav.foreldrepenger.lookup.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.lookup.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.lookup.Constants.X_CORRELATION_ID;
import static no.nav.foreldrepenger.lookup.util.MDCUtil.callId;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.CallIdGenerator;

@Component
public class MDCValuesPropagatingClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private final CallIdGenerator callIdGenerator;

    public MDCValuesPropagatingClientRequestInterceptor(CallIdGenerator callIdGenerator) {
        this.callIdGenerator = callIdGenerator;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        propagateIfSet(req, NAV_CALL_ID, NAV_CONSUMER_ID);
        propagate(req, X_CORRELATION_ID, callId());
        return execution.execute(req, body);
    }

    private void propagateIfSet(HttpRequest request, String... keys) {
        for (String key : keys) {
            String value = MDC.get(key);
            if (value != null) {
                propagate(request, key, value);
            }
        }
    }

    private static void propagate(HttpRequest request, String key, String value) {
        request.getHeaders().set(key, value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [callIdGenerator=" + callIdGenerator + "]";
    }
}

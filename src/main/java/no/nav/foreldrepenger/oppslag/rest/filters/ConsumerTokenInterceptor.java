package no.nav.foreldrepenger.oppslag.rest.filters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class ConsumerTokenInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerTokenInterceptor.class);

    private final SystemUserTokenService userTokenService;

    public ConsumerTokenInterceptor(SystemUserTokenService userTokenService) {
        this.userTokenService = userTokenService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        LOG.debug("Adding \'Nav-Consumer-Token\' header to {} {}", request.getMethod(), request.getURI());
        request.getHeaders().add("Nav-Consumer-Token",
                "Bearer " + userTokenService.fetch().getAccessToken());
        return execution.execute(request, body);
    }

}

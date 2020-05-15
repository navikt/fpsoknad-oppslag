package no.nav.foreldrepenger.oppslag.rest.filters;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

import java.io.IOException;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;

@Component
public class TimingAndLoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(TimingAndLoggingClientHttpRequestInterceptor.class);

    private final TokenUtil tokenUtil;

    public TimingAndLoggingClientHttpRequestInterceptor(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        LOG.info("{} - {}", request.getMethodValue(), request.getURI());
        var timer = new StopWatch();
        timer.start();
        ClientHttpResponse respons = execution.execute(request, body);
        timer.stop();
        if (hasError(respons.getStatusCode())) {
            LOG.warn("{} - {} - ({}). Dette tok {}ms. ({})", request.getMethodValue(), request.getURI(),
                    respons.getRawStatusCode(), timer.getTime(MILLISECONDS), tokenUtil.getExpiryDate());
        } else {
            LOG.info("{} - {} - ({}). Dette tok {}ms", request.getMethodValue(), request.getURI(),
                    respons.getRawStatusCode(), timer.getTime(MILLISECONDS));
        }
        return respons;
    }

    protected boolean hasError(HttpStatus code) {
        return (code.series() == CLIENT_ERROR) || (code.series() == SERVER_ERROR);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [tokenUtil=" + tokenUtil + "]";
    }
}
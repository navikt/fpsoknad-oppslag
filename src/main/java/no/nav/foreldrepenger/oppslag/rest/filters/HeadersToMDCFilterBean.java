package no.nav.foreldrepenger.oppslag.rest.filters;

import static no.nav.foreldrepenger.oppslag.config.Constants.NAV_CALL_ID;
import static no.nav.foreldrepenger.oppslag.config.Constants.NAV_CONSUMER_ID;
import static no.nav.foreldrepenger.oppslag.util.MDCUtil.tilMDC;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.oppslag.util.CallIdGenerator;

@Component
public class HeadersToMDCFilterBean extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(HeadersToMDCFilterBean.class);

    private final CallIdGenerator generator;
    private final String applicationName;

    @Inject
    public HeadersToMDCFilterBean(CallIdGenerator generator,
            @Value("${spring.application.name}") String applicationName) {
        this.generator = generator;
        this.applicationName = applicationName;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        putValues(HttpServletRequest.class.cast(request));
        chain.doFilter(request, response);
    }

    private void putValues(HttpServletRequest request) {
        try {
            tilMDC(NAV_CONSUMER_ID, request.getHeader(NAV_CONSUMER_ID), applicationName);
            tilMDC(NAV_CALL_ID, request.getHeader(NAV_CALL_ID), generator.create());
        } catch (Exception e) {
            LOG.warn("Noe gikk feil ved propagering av header-verdier for request {}, MDC-verdier er inkomplette",
                    request.getRequestURL(), e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [generator=" + generator + ", applicationName=" + applicationName + "]";
    }

}

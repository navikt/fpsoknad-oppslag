package no.nav.foreldrepenger.lookup.rest.filters;

import static no.nav.foreldrepenger.lookup.Constants.NAV_AKTØR_ID;
import static no.nav.foreldrepenger.lookup.Constants.NAV_USER_ID;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.isDevOrPreprod;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

@Order(LOWEST_PRECEDENCE)
@Component
public class IDToMDCFilterBean extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(IDToMDCFilterBean.class);

    private final AktorIdClient aktørIdClient;
    private final TokenUtil helper;

    public IDToMDCFilterBean(TokenUtil helper, AktorIdClient aktørIdClient) {
        this.helper = helper;
        this.aktørIdClient = aktørIdClient;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (helper.erAutentisert()) {
            copyHeadersToMDC();
        }
        chain.doFilter(req, res);
    }

    private void copyHeadersToMDC() {
        try {
            Fødselsnummer fnr = helper.getSubject();
            if (isDevOrPreprod(getEnvironment())) {
                MDC.put(NAV_USER_ID, fnr.getFnr());
            }
            putValue(NAV_AKTØR_ID, aktørIdClient.aktorIdForFnr(fnr).getAktør());
        } catch (Exception e) {
            LOG.info("Noe gikk feil ved henting av aktørId. ikke kritisk, men MDC-verdier er inkomplette", e);
        }
    }

    private static void putValue(String key, String value) {
        putValue(key, value, null);
    }

    private static void putValue(String key, String value, String defaultValue) {
        MDC.put(key, Optional.ofNullable(value).orElse(defaultValue));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktørIdClient=" + aktørIdClient + ", helper=" + helper + "]";
    }
}

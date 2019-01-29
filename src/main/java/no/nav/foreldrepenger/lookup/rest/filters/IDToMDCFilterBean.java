package no.nav.foreldrepenger.lookup.rest.filters;

import static no.nav.foreldrepenger.lookup.Constants.NAV_AKTØR_ID;
import static no.nav.foreldrepenger.lookup.Constants.NAV_TOKEN_EXPIRY_ID;
import static no.nav.foreldrepenger.lookup.Constants.NAV_USER_ID;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.isDevOrPreprod;
import static no.nav.foreldrepenger.lookup.util.MDCUtil.toMDC;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            copyHeadersToMDC(HttpServletRequest.class.cast(req));
        }
        chain.doFilter(req, res);
    }

    private void copyHeadersToMDC(HttpServletRequest req) {
        try {
            String fnr = helper.getSubject();
            if (isDevOrPreprod(getEnvironment())) {
                toMDC(NAV_USER_ID, fnr);
            }
            if (helper.getExpiryDate() != null) {
                toMDC(NAV_TOKEN_EXPIRY_ID, helper.getExpiryDate().toString(), null);
            }
            toMDC(NAV_AKTØR_ID, aktørIdClient.aktorIdForFnr(new Fødselsnummer(fnr)).getAktør());
        } catch (Exception e) {
            LOG.warn("Noe gikk galt ved setting av MDC-verdier for request {}, MDC-verdier er inkomplette",
                    req.getRequestURI(), e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktørIdClient=" + aktørIdClient + ", helper=" + helper + "]";
    }
}

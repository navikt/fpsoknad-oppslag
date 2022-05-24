package no.nav.foreldrepenger.oppslag.rest.filters;

import static no.nav.foreldrepenger.oppslag.config.Constants.NAV_TOKEN_EXPIRY_ID;
import static no.nav.foreldrepenger.oppslag.config.Constants.NAV_USER_ID;
import static no.nav.foreldrepenger.oppslag.util.MDCUtil.tilMDC;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import no.nav.foreldrepenger.oppslag.util.StringUtil;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;

@Component
public class IDToMDCFilterBean extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(IDToMDCFilterBean.class);

    private final TokenUtil tokenUtil;

    public IDToMDCFilterBean(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        var httpServletRequest = (HttpServletRequest) req;
        headersTilMDC(httpServletRequest);
        chain.doFilter(req, res);
    }

    private void headersTilMDC(HttpServletRequest req) {
        try {
            if (tokenUtil.erAutentisert()) {
                tilMDC(NAV_USER_ID, Optional.ofNullable(tokenUtil.getSubject()).map(StringUtil::mask).orElse("Uautentisert"));
                tilMDC(NAV_TOKEN_EXPIRY_ID, tokenUtil.getExpiryDate().toString(), null);
            }
        } catch (Exception e) {
            LOG.warn("Noe gikk galt ved setting av MDC-verdier for request {}, MDC-verdier er inkomplette", req.getRequestURI(), e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [tokenUtil=" + tokenUtil + "]";
    }
}

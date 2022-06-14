package no.nav.foreldrepenger.oppslag.rest.filters;

import static no.nav.foreldrepenger.common.util.Constants.NAV_USER_ID;
import static no.nav.foreldrepenger.common.util.MDCUtil.toMDC;
import static no.nav.foreldrepenger.common.util.TokenUtil.NAV_AUTH_LEVEL;
import static no.nav.foreldrepenger.common.util.TokenUtil.NAV_TOKEN_EXPIRY_ID;

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

import no.nav.foreldrepenger.common.util.AuthenticationLevel;
import no.nav.foreldrepenger.common.util.StringUtil;
import no.nav.foreldrepenger.common.util.TokenUtil;

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
                toMDC(NAV_USER_ID, Optional.ofNullable(tokenUtil.getSubject()).map(StringUtil::mask).orElse("Uautentisert"));
                toMDC(NAV_AUTH_LEVEL, Optional.ofNullable(tokenUtil.getLevel()).map(AuthenticationLevel::name).orElse(AuthenticationLevel.NONE.name()));
                toMDC(NAV_TOKEN_EXPIRY_ID, tokenUtil.getExpiration());
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

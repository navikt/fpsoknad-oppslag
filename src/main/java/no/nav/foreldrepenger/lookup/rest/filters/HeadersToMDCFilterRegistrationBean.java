package no.nav.foreldrepenger.lookup.rest.filters;

import static no.nav.foreldrepenger.lookup.OppslagController.OPPSLAG;
import static no.nav.foreldrepenger.lookup.rest.filters.FilterRegistrationUtil.urlPatternsFor;
import static no.nav.foreldrepenger.lookup.rest.sak.SakController.SAK;
import static no.nav.foreldrepenger.lookup.ws.arbeidsforhold.ArbeidsforholdController.ARBEIDSFORHOLD;
import static no.nav.foreldrepenger.lookup.ws.person.PersonController.PERSON;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(HIGHEST_PRECEDENCE)
public class HeadersToMDCFilterRegistrationBean extends FilterRegistrationBean<HeadersToMDCFilterBean> {
    private static final Logger LOG = LoggerFactory.getLogger(HeadersToMDCFilterRegistrationBean.class);

    public HeadersToMDCFilterRegistrationBean(HeadersToMDCFilterBean headersFilter) {
        setFilter(headersFilter);
        setUrlPatterns(urlPatternsFor(OPPSLAG, ARBEIDSFORHOLD, PERSON, SAK));
        LOG.info("Registrert filter {}", this);
    }
}

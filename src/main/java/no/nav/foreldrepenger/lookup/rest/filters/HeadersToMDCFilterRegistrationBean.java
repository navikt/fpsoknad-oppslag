package no.nav.foreldrepenger.lookup.rest.filters;

import static no.nav.foreldrepenger.lookup.rest.filters.FilterRegistrationUtil.always;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(HIGHEST_PRECEDENCE)
public class HeadersToMDCFilterRegistrationBean extends FilterRegistrationBean<HeadersToMDCFilterBean> {

    public HeadersToMDCFilterRegistrationBean(HeadersToMDCFilterBean headersFilter) {
        setFilter(headersFilter);
        setUrlPatterns(always());
    }
}

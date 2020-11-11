package no.nav.foreldrepenger.oppslag.rest.filters;

import static no.nav.foreldrepenger.oppslag.rest.filters.FilterRegistrationUtil.always;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

@Component
public class IDFilterRegistrationBean extends FilterRegistrationBean<IDToMDCFilterBean> {

    private static final Logger LOG = LoggerFactory.getLogger(HeadersToMDCFilterRegistrationBean.class);

    public IDFilterRegistrationBean(IDToMDCFilterBean idFilter) {
        setFilter(idFilter);
        setUrlPatterns(always());
        LOG.info("Registrert filter {}", this);
    }
}

package no.nav.foreldrepenger.lookup.rest.filters;

import static no.nav.foreldrepenger.lookup.OppslagController.OPPSLAG;
import static no.nav.foreldrepenger.lookup.rest.filters.FilterRegistrationUtil.urlPatternsFor;
import static no.nav.foreldrepenger.lookup.rest.sak.SakController.SAK;
import static no.nav.foreldrepenger.lookup.ws.arbeidsforhold.ArbeidsforholdController.ARBEIDSFORHOLD;
import static no.nav.foreldrepenger.lookup.ws.person.PersonController.PERSON;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

@Component
public class IDFilterRegistrationBean extends FilterRegistrationBean<IDToMDCFilterBean> {

    public IDFilterRegistrationBean(IDToMDCFilterBean idFilter) {
        setFilter(idFilter);
        setUrlPatterns(urlPatternsFor(OPPSLAG, ARBEIDSFORHOLD, PERSON, SAK));
    }
}

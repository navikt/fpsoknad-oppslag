package no.nav.foreldrepenger.oppslag.stub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import no.nav.foreldrepenger.oppslag.rest.sak.SakClient;
import no.nav.foreldrepenger.oppslag.util.EnvUtil;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.ArbeidsforholdClient;
import no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.OrganisasjonClient;
import no.nav.foreldrepenger.oppslag.ws.person.PersonClient;

@Configuration
@Profile(EnvUtil.LOCAL)
public class StubConfiguration {

    @Bean
    @Primary
    public AktorIdClient getAktorIdClientStub() {
        return new AktorIdClientStub();
    }

    @Bean
    @Primary
    public PersonClient getPersonClientStub() {
        return new PersonClientStub();
    }

    @Bean
    @Primary
    public ArbeidsforholdClient getAaregClientStub() {
        return new ArbeidsforholdClientStub();
    }

    @Bean
    @Primary
    public OrganisasjonClient organisasjonClientStub() {
        return new OrganisasjonClientStub();
    }

    @Bean
    @Primary
    public SakClient sakClientStub() {
        return new SakClientStub();
    }
}

package no.nav.foreldrepenger.oppslag.stub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import no.nav.foreldrepenger.oppslag.util.EnvUtil;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørTjeneste;
import no.nav.foreldrepenger.oppslag.ws.person.PersonTjeneste;

@Configuration
@Profile(EnvUtil.LOCAL)
public class StubConfiguration {

    @Bean
    @Primary
    public AktørTjeneste getAktorIdClientStub() {
        return new AktorIdClientStub();
    }

    @Bean
    @Primary
    public PersonTjeneste getPersonClientStub() {
        return new PersonClientStub();
    }

}

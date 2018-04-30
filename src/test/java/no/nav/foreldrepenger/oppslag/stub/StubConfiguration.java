package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.aareg.AaregClient;
import no.nav.foreldrepenger.oppslag.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.arena.ArenaClient;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakClient;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdClient;
import no.nav.foreldrepenger.oppslag.inntekt.InntektClient;
import no.nav.foreldrepenger.oppslag.medl.MedlClient;
import no.nav.foreldrepenger.oppslag.person.PersonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
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
    public AaregClient getAaregClientStub() {
        return new AaregClientStub();
    }

    @Bean
    @Primary
    public ArenaClient getArenaClientStub() {
        return new ArenaClientStub();
    }

    @Bean
    @Primary
    public FpsakClient getFpsakClientStub() {
        return new FpsakClientStub();
    }

    @Bean
    @Primary
    public InfotrygdClient getInfotrygdClientStub() {
        return new InfotrygdClientStub();
    }

    @Bean
    @Primary
    public InntektClient getInntektClientStub() {
        return new InntektClientStub();
    }

    @Bean
    @Primary
    public MedlClient getMedlClientStub() {
        return new MedlClientStub();
    }
}

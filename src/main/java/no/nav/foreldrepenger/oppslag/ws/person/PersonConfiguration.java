package no.nav.foreldrepenger.oppslag.ws.person;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
public class PersonConfiguration extends WsClient<PersonV3> {

    public static final String PERSON_V3 = "person";
    private static final String HEALTH_INDICATOR_PERSON = "healthIndicatorPerson";

    @Bean
    @Qualifier(PERSON_V3)
    public PersonV3 personV3(@Value("${virksomhet.person.v3.endpointurl}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, PersonV3.class);
    }

    @Bean
    @Qualifier(HEALTH_INDICATOR_PERSON)
    public PersonV3 healthIndicatorPerson(@Value("${virksomhet.person.v3.endpointurl}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, PersonV3.class);
    }

    @Bean
    public Barnutvelger barnutvelger(@Value("${foreldrepenger.selvbetjening.maxmonthsback:24}") int months) {
        return new BarnMorRelasjonSjekkendeBarnutvelger(months);
    }

    @Bean
    public PersonTjeneste personKlientTpsWs(@Qualifier(PERSON_V3) PersonV3 personV3,
            @Qualifier(HEALTH_INDICATOR_PERSON) PersonV3 healthIndicator, TokenUtil handler,
            Barnutvelger barnutvelger) {
        return new PersonClientTpsWs(personV3, healthIndicator, handler, barnutvelger);
    }
}

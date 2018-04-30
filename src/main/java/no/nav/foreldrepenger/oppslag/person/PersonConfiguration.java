package no.nav.foreldrepenger.oppslag.person;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
public class PersonConfiguration extends WsClient<PersonV3>{

    @SuppressWarnings("unchecked")
    @Bean
    public PersonV3 personV3(@Value("${VIRKSOMHET_PERSON_V3_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, PersonV3.class);
    }

    @Bean
    public PersonClient personKlientTpsWs(PersonV3 person) {
        return new PersonClientTpsWs(person);
    }

}

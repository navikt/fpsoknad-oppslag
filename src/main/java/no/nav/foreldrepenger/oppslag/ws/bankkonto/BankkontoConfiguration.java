package no.nav.foreldrepenger.oppslag.ws.bankkonto;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
public class BankkontoConfiguration extends WsClient<PersonV3> {

    public static final String PERSON_V3 = "person";
    private static final String HEALTH_INDICATOR_PERSON = "healthIndicatorPerson";

    @Bean
    @Qualifier(PERSON_V3)
    public PersonV3 client(@Value("${virksomhet.person.v3.endpointurl}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, PersonV3.class);
    }

    @Bean
    @Qualifier(HEALTH_INDICATOR_PERSON)
    public PersonV3 healthIndicator(@Value("${virksomhet.person.v3.endpointurl}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, PersonV3.class);
    }

    @Bean
    public BankkontoTjeneste bankkontoKlient(@Qualifier(PERSON_V3) PersonV3 client,
            @Qualifier(HEALTH_INDICATOR_PERSON) PersonV3 healthIndicator, TokenUtil handler) {
        return new BankkontoClientWs(healthIndicator, /* client, */ healthIndicator, handler);
    }
}

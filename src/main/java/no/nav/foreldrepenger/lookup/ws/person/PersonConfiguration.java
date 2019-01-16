package no.nav.foreldrepenger.lookup.ws.person;

import static no.nav.foreldrepenger.lookup.util.RetryUtil.retry;
import static org.slf4j.LoggerFactory.getLogger;

import javax.xml.ws.soap.SOAPFaultException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.retry.Retry;
import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;

@Configuration
public class PersonConfiguration extends WsClient<PersonV3> {

    private static final String PERSON_V3 = "person";
    private static final String HEALTH_INDICATOR_PERSON = "healthIndicatorPerson";
    private static final String PERSONV3_RETRY = "personV3retry";

    @Bean
    @Qualifier(PERSON_V3)
    public PersonV3 personV3(@Value("${VIRKSOMHET_PERSON_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, PersonV3.class);
    }

    @Bean
    @Qualifier(HEALTH_INDICATOR_PERSON)
    public PersonV3 healthIndicatorPerson(@Value("${VIRKSOMHET_PERSON_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, PersonV3.class);
    }

    @Bean
    public Barnutvelger barnutvelger(PersonV3 personV3,
            @Value("${foreldrepenger.selvbetjening.maxmonthsback:24}") int months) {
        return new BarnMorRelasjonSjekkendeBarnutvelger(months);
    }

    @Bean
    public PersonClient personKlientTpsWs(@Qualifier(PERSON_V3) PersonV3 personV3,
            @Qualifier(HEALTH_INDICATOR_PERSON) PersonV3 healthIndicator, TokenUtil handler,
            Barnutvelger barnutvelger, @Qualifier(PERSONV3_RETRY) Retry retry) {
        return new PersonClientTpsWs(personV3, healthIndicator, handler, barnutvelger, retry);
    }

    @Bean
    @Qualifier(PERSONV3_RETRY)
    public Retry personRetry(@Value("${retry.tps.max:2}") int max) {
        return retry(max, PERSON_V3, SOAPFaultException.class, getLogger(PersonClientTpsWs.class));
    }

}

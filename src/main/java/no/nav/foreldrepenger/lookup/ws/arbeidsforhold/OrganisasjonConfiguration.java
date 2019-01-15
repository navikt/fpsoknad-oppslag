package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.retry.Retry;
import no.nav.foreldrepenger.lookup.TokenHandler;
import no.nav.foreldrepenger.lookup.util.RetryUtil;
import no.nav.foreldrepenger.lookup.ws.WsClient;
import no.nav.tjeneste.virksomhet.organisasjon.v5.binding.OrganisasjonV5;

@Configuration
public class OrganisasjonConfiguration extends WsClient<OrganisasjonV5> {

    private static final String ORGANISASJON_V5 = "organisasjonV5";
    private static final String HEALTH_INDICATOR_ORGANISASJON = "healthIndicatorOrganisasjon";
    private static final String ORGANISASJON_V5V3RETRY = "organisasjonV5V3retry";

    @Bean
    @Qualifier(ORGANISASJON_V5)
    public OrganisasjonV5 organisasjonV5(
            @Value("${VIRKSOMHET_ORGANISASJON_V5_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, OrganisasjonV5.class);
    }

    @Bean
    @Qualifier(HEALTH_INDICATOR_ORGANISASJON)
    public OrganisasjonV5 healthIndicatorOrganissjon(
            @Value("${VIRKSOMHET_ORGANISASJON_V5_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, OrganisasjonV5.class);
    }

    @Bean
    public OrganisasjonClient organisasjonClientWs(@Qualifier(ORGANISASJON_V5) OrganisasjonV5 organisasjonV5,
            @Qualifier(HEALTH_INDICATOR_ORGANISASJON) OrganisasjonV5 healthIndicator, TokenHandler tokenHandler,
            @Qualifier(ORGANISASJON_V5V3RETRY) Retry retry) {
        return new OrganisasjonClientWs(organisasjonV5, healthIndicator, tokenHandler, retry);
    }

    @Bean
    @Qualifier(ORGANISASJON_V5V3RETRY)
    public Retry retryConfig(@Value("${retry.organisasjon.max:2}") int max) {
        return RetryUtil.retry(max, "Organisasjon", SOAPFaultException.class,
                LoggerFactory.getLogger(OrganisasjonClientWs.class));
    }
}

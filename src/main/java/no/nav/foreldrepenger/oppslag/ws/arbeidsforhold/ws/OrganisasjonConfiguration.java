package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.ws;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.organisasjon.v5.binding.OrganisasjonV5;

@Configuration
public class OrganisasjonConfiguration extends WsClient<OrganisasjonV5> {

    public static final String ORGANISASJON_V5 = "organisasjonV5";
    private static final String HEALTH_INDICATOR_ORGANISASJON = "healthIndicatorOrganisasjon";

    @Bean
    @Qualifier(ArbeidsforholdConfiguration.ARBEIDSFORHOLD_V3)
    public OrganisasjonV5 organisasjonV5(
            @Value("${VIRKSOMHET_ORGANISASJON_V5_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, OrganisasjonV5.class);
    }

    @Bean
    @Qualifier(HEALTH_INDICATOR_ORGANISASJON)
    public OrganisasjonV5 healthIndicatorOrganisasjon(
            @Value("${VIRKSOMHET_ORGANISASJON_V5_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, OrganisasjonV5.class);
    }

    @Bean
    public OrganisasjonClient organisasjonClientWs(@Qualifier(ORGANISASJON_V5) OrganisasjonV5 organisasjonV5,
            @Qualifier(HEALTH_INDICATOR_ORGANISASJON) OrganisasjonV5 healthIndicator, TokenUtil tokenHandler) {
        return new OrganisasjonClientWs(organisasjonV5, healthIndicator, tokenHandler);
    }

}

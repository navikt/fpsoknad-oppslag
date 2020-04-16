package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;

@Configuration
public class ArbeidsforholdConfiguration extends WsClient<ArbeidsforholdV3> {

    private static final String HEALTH_INDICATOR_AAREG = "healthIndicatorAareg";
    public static final String ARBEIDSFORHOLD_V3 = "arbeidsforholdV3";

    @Bean
    @Qualifier(ARBEIDSFORHOLD_V3)
    public ArbeidsforholdV3 arbeidsforholdV3(
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, ArbeidsforholdV3.class);
    }

    @Bean
    @Qualifier(HEALTH_INDICATOR_AAREG)
    public ArbeidsforholdV3 healthIndicatorAareg(
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, ArbeidsforholdV3.class);
    }

    @Bean
    public ArbeidsforholdTjeneste aaregClientWs(@Qualifier(ARBEIDSFORHOLD_V3) ArbeidsforholdV3 arbeidsforholdV3,
            @Qualifier(HEALTH_INDICATOR_AAREG) ArbeidsforholdV3 healthIndicator,
            OrganisasjonClient organisasjonClient, TokenUtil tokenHandler) {
        return new ArbeidsforholdClientWs(arbeidsforholdV3, healthIndicator, organisasjonClient, tokenHandler);
    }

}

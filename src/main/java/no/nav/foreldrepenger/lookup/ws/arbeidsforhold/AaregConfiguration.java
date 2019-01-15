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
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;

@Configuration
public class AaregConfiguration extends WsClient<ArbeidsforholdV3> {

    private static final String ARBEIDSFORHOLD_V3RETRY = "arbeidsforholdV3retry";

    @Bean
    @Qualifier("arbeidsforholdV3")
    public ArbeidsforholdV3 arbeidsforholdV3(
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, ArbeidsforholdV3.class);
    }

    @Bean
    @Qualifier("healthIndicatorAareg")
    public ArbeidsforholdV3 healthIndicatorAareg(
            @Value("${VIRKSOMHET_ARBEIDSFORHOLD_V3_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, ArbeidsforholdV3.class);
    }

    @Bean
    public ArbeidsforholdClient aaregClientWs(@Qualifier("arbeidsforholdV3") ArbeidsforholdV3 arbeidsforholdV3,
            @Qualifier("healthIndicatorAareg") ArbeidsforholdV3 healthIndicator,
            OrganisasjonClient organisasjonClient, TokenHandler tokenHandler,
            @Qualifier(ARBEIDSFORHOLD_V3RETRY) Retry retryConfig) {
        return new ArbeidsforholdClientWs(arbeidsforholdV3, healthIndicator, organisasjonClient, tokenHandler,
                retryConfig);
    }

    @Bean
    @Qualifier(ARBEIDSFORHOLD_V3RETRY)
    public Retry retryConfig(@Value("${retry.aareg.max:2}") int max) {
        return RetryUtil.retry(max, "AAREG", SOAPFaultException.class,
                LoggerFactory.getLogger(ArbeidsforholdClientWs.class));
    }
}

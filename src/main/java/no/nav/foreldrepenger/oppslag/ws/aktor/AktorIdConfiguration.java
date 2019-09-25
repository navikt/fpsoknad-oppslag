package no.nav.foreldrepenger.oppslag.ws.aktor;

import static org.slf4j.LoggerFactory.getLogger;

import javax.xml.ws.soap.SOAPFaultException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.retry.Retry;
import no.nav.foreldrepenger.oppslag.util.RetryUtil;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;

@Configuration
public class AktorIdConfiguration extends WsClient<AktoerV2> {

    private static final String HEALTH_INDICATOR_AKTØR = "healthIndicatorAktør";
    private static final String AKTOER_V2 = "aktoerV2";
    private static final String AKTOER_V2RETRY = "aktoerV2retry";

    @Bean
    @Qualifier(AKTOER_V2)
    public AktoerV2 aktoerV2(@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, AktoerV2.class);
    }

    @Bean
    @Qualifier(HEALTH_INDICATOR_AKTØR)
    public AktoerV2 healthIndicatorAktør(@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, AktoerV2.class);
    }

    @Bean
    public AktorIdClient aktorIdClientWs(@Qualifier(AKTOER_V2) AktoerV2 aktoerV2,
            @Qualifier(HEALTH_INDICATOR_AKTØR) AktoerV2 healthIndicator, TokenUtil tokenHandler,
            @Qualifier(AKTOER_V2RETRY) Retry retryConfig) {
        return new AktorIdClientWs(aktoerV2, healthIndicator, tokenHandler, retryConfig);
    }

    @Bean
    @Qualifier(AKTOER_V2RETRY)
    public Retry aktørRetry(@Value("${retry.aktør.max:2}") int max) {
        return RetryUtil.retry(max, "aktør/fnr", SOAPFaultException.class, getLogger(AktorIdClientWs.class));
    }
}

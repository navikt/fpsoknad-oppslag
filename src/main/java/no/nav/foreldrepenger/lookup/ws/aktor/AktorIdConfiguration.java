package no.nav.foreldrepenger.lookup.ws.aktor;

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
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;

@Configuration
public class AktorIdConfiguration extends WsClient<AktoerV2> {

    private static final String AKTOER_V2RETRY = "aktoerV2retry";

    @Bean
    @Qualifier("aktoerV2")
    public AktoerV2 aktoerV2(@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, AktoerV2.class);
    }

    @Bean
    @Qualifier("healthIndicatorAktør")
    public AktoerV2 healthIndicatorAktør(@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, AktoerV2.class);
    }

    @Bean
    public AktorIdClient aktorIdClientWs(@Qualifier("aktoerV2") AktoerV2 aktoerV2,
            @Qualifier("healthIndicatorAktør") AktoerV2 healthIndicator, TokenHandler tokenHandler,
            @Qualifier(AKTOER_V2RETRY) Retry retryConfig) {
        return new AktorIdClientWs(aktoerV2, healthIndicator, tokenHandler, retryConfig);
    }

    @Bean
    @Qualifier(AKTOER_V2RETRY)
    public Retry aktørRetry(@Value("${retry.aktør.max:2}") int max) {
        return RetryUtil.retry(max, "aktør/fnr", SOAPFaultException.class,
                LoggerFactory.getLogger(AktorIdClientWs.class));
    }
}

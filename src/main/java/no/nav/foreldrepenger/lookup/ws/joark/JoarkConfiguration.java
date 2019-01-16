package no.nav.foreldrepenger.lookup.ws.joark;

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
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;

@Configuration
public class JoarkConfiguration extends WsClient<InnsynJournalV2> {

    private static final String JOARK_V2 = "joarkV2";
    private static final String HEALTH_INDICATOR_JOARK = "healthIndicatorJoarkV2";
    private static final String JOARK_V2RETRY = "joarkV2retry";

    @Bean
    @Qualifier(JOARK_V2)
    public InnsynJournalV2 joarkV2(
            @Value("${JOURNAL_V2_ENDPOINTURL}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, InnsynJournalV2.class);
    }

    @Bean
    @Qualifier(HEALTH_INDICATOR_JOARK)
    public InnsynJournalV2 healthIndicatorJoark(
            @Value("${JOURNAL_V2_ENDPOINTURL}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, InnsynJournalV2.class);
    }

    @Bean
    public JoarkClient joarkClientWs(@Qualifier(JOARK_V2) InnsynJournalV2 joarkV2,
            @Qualifier(HEALTH_INDICATOR_JOARK) InnsynJournalV2 healthIndicator, TokenUtil tokenHandler,
            @Qualifier(JOARK_V2RETRY) Retry retry) {
        return new JoarkClientWs(joarkV2, healthIndicator, tokenHandler, retry);
    }

    @Bean
    @Qualifier(JOARK_V2RETRY)
    public Retry joarketryConfig(@Value("${retry.joark.max:2}") int max) {
        return retry(max, "joark", SOAPFaultException.class, getLogger(JoarkClientWs.class));
    }
}

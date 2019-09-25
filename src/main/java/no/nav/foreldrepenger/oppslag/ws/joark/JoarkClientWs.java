package no.nav.foreldrepenger.oppslag.ws.joark;

import static no.nav.foreldrepenger.oppslag.util.RetryUtil.DEFAULT_RETRIES;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.retry.Retry;
import no.nav.foreldrepenger.oppslag.util.RetryUtil;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.ArbeidsforholdClientWs;
import no.nav.tjeneste.virksomhet.innsynjournal.v2.binding.InnsynJournalV2;

public class JoarkClientWs implements JoarkClient {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdClientWs.class);

    private final InnsynJournalV2 innsynV2;
    private final InnsynJournalV2 healthIndicator;
    private final TokenUtil tokenUtil;
    private final Retry retryConfig;

    JoarkClientWs(InnsynJournalV2 innsynV2, InnsynJournalV2 healthIndicator, TokenUtil tokenUtil) {
        this(innsynV2, healthIndicator, tokenUtil, retryConfig());
    }

    public JoarkClientWs(InnsynJournalV2 innsynV2, InnsynJournalV2 healthIndicator, TokenUtil tokenUtil,
            Retry retry) {
        this.innsynV2 = innsynV2;
        this.healthIndicator = healthIndicator;
        this.tokenUtil = tokenUtil;
        this.retryConfig = retry;
    }

    @Override
    public void ping() {
        LOG.info("Pinger Joark");
        healthIndicator.ping();
    }

    private static Retry retryConfig() {
        return RetryUtil.retry(DEFAULT_RETRIES, "joark", SOAPFaultException.class, LOG);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [innsynV2=" + innsynV2 + ", healthIndicator="
                + healthIndicator + ", tokenUtil=" + tokenUtil + ", retryConfig="
                + retryConfig + "]";
    }

}

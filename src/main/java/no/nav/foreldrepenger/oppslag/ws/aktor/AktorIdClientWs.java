package no.nav.foreldrepenger.oppslag.ws.aktor;

import static io.github.resilience4j.retry.Retry.decorateSupplier;
import static no.nav.foreldrepenger.oppslag.config.Constants.NAV_AKTØR_ID;
import static no.nav.foreldrepenger.oppslag.util.EnvUtil.CONFIDENTIAL;

import java.util.Objects;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.retry.Retry;
import no.nav.foreldrepenger.oppslag.errorhandling.NotFoundException;
import no.nav.foreldrepenger.oppslag.errorhandling.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.util.MDCUtil;
import no.nav.foreldrepenger.oppslag.util.RetryUtil;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdRequest;

public class AktorIdClientWs implements AktorIdClient {
    private static final Logger LOG = LoggerFactory.getLogger(AktorIdClientWs.class);

    private final AktoerV2 aktoerV2;
    private final AktoerV2 healthIndicator;
    private final TokenUtil tokenUtil;
    private final Retry retryConfig;

    AktorIdClientWs(AktoerV2 aktoerV2, AktoerV2 healthIndicator, TokenUtil tokenUtil) {
        this(aktoerV2, healthIndicator, tokenUtil, defaultRetryConfig());
    }

    public AktorIdClientWs(AktoerV2 aktoerV2, AktoerV2 healthIndicator, TokenUtil tokenUtil, Retry retryConfig) {
        this.aktoerV2 = Objects.requireNonNull(aktoerV2);
        this.healthIndicator = Objects.requireNonNull(healthIndicator);
        this.tokenUtil = tokenUtil;
        this.retryConfig = retryConfig;
    }

    @Override
    // @Cacheable(cacheNames = "aktoer")
    public AktorId aktorIdForFnr(Fødselsnummer fnr) {
        return new AktorId(decorateSupplier(retryConfig, () -> hentAktør(fnr)).get());
    }

    @Override
    public Fødselsnummer fnrForAktørId(AktorId aktørId) {
        return new Fødselsnummer(decorateSupplier(retryConfig, () -> hentId(aktørId)).get());
    }

    @Override
    public void ping() {
        LOG.info("Pinger Aktørregisteret");
        healthIndicator.ping();
    }

    private String hentAktør(Fødselsnummer fnr) {
        LOG.info(CONFIDENTIAL, "Henter aktør for fnr {}", fnr.getFnr());
        try {
            String aktørId = aktoerV2.hentAktoerIdForIdent(request(fnr)).getAktoerId();
            MDCUtil.toMDC(NAV_AKTØR_ID, aktørId);
            LOG.info(CONFIDENTIAL, "Aktørid for {} er {}", fnr.getFnr(), aktørId);
            return aktørId;
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            throw new NotFoundException(fnr.getFnr(), e);
        } catch (SOAPFaultException e) {
            LOG.trace("Feil ved henting av aktør", e);
            if (tokenUtil.isExpired()) {
                LOG.trace("Token expired: {}", tokenUtil.getExpiryDate());
                throw new TokenExpiredException(tokenUtil.getExpiryDate(), e);
            }
            throw e;
        }
    }

    private String hentId(AktorId aktørId) {
        try {
            return aktoerV2.hentIdentForAktoerId(request(aktørId)).getIdent();
        } catch (HentIdentForAktoerIdPersonIkkeFunnet e) {
            throw new NotFoundException(aktørId.getAktør(), e);
        } catch (SOAPFaultException e) {
            if (tokenUtil.isExpired()) {
                throw new TokenExpiredException(tokenUtil.getExpiryDate(), e);
            }
            throw e;
        }
    }

    private static HentIdentForAktoerIdRequest request(AktorId aktørId) {
        HentIdentForAktoerIdRequest req = new HentIdentForAktoerIdRequest();
        req.setAktoerId(aktørId.getAktør());
        return req;
    }

    private static HentAktoerIdForIdentRequest request(Fødselsnummer fnr) {
        HentAktoerIdForIdentRequest req = new HentAktoerIdForIdentRequest();
        req.setIdent(fnr.getFnr());
        return req;
    }

    private static Retry defaultRetryConfig() {
        return RetryUtil.retry(2, "aktør/fnr", SOAPFaultException.class, LOG);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktoerV2=" + aktoerV2 + ", healthIndicator=" + healthIndicator
                + ", tokenUtil=" + tokenUtil + ", retryConfig=" + retryConfig + "]";
    }
}

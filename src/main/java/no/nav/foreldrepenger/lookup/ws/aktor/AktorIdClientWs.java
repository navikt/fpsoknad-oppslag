package no.nav.foreldrepenger.lookup.ws.aktor;

import static io.github.resilience4j.retry.Retry.decorateSupplier;

import java.util.Objects;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import io.github.resilience4j.retry.Retry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.errorhandling.NotFoundException;
import no.nav.foreldrepenger.errorhandling.TokenExpiredException;
import no.nav.foreldrepenger.lookup.TokenHandler;
import no.nav.foreldrepenger.lookup.util.RetryUtil;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdRequest;

public class AktorIdClientWs implements AktorIdClient {
    private static final Logger LOG = LoggerFactory.getLogger(AktorIdClientWs.class);

    private final AktoerV2 aktoerV2;
    private final AktoerV2 healthIndicator;
    private final TokenHandler tokenHandler;
    private final Retry retry;

    private static final Counter ERROR_COUNTER_AKTOR = Metrics.counter("errors.lookup.aktorid");
    private static final Counter ERROR_COUNTER_FNR = Metrics.counter("errors.lookup.fnr");

    AktorIdClientWs(AktoerV2 aktoerV2, AktoerV2 healthIndicator, TokenHandler tokenHandler) {
        this(aktoerV2, healthIndicator, tokenHandler, retry());
    }

    public AktorIdClientWs(AktoerV2 aktoerV2, AktoerV2 healthIndicator, TokenHandler tokenHandler, Retry retry) {
        this.aktoerV2 = Objects.requireNonNull(aktoerV2);
        this.healthIndicator = Objects.requireNonNull(healthIndicator);
        this.tokenHandler = tokenHandler;
        this.retry = retry;
    }

    @Override
    @Cacheable(cacheNames = "aktoer")
    public AktorId aktorIdForFnr(Fødselsnummer fnr) {
        return new AktorId(decorateSupplier(retry, () -> hentAktør(fnr)).get());
    }

    @Override
    public Fødselsnummer fnrForAktørId(AktorId aktørId) {
        return new Fødselsnummer(decorateSupplier(retry, () -> hentId(aktørId)).get());
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger Aktørregisteret");
            healthIndicator.ping();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private String hentAktør(Fødselsnummer fnr) {
        try {
            return aktoerV2.hentAktoerIdForIdent(request(fnr)).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            throw new NotFoundException(e);
        } catch (SOAPFaultException e) {
            ERROR_COUNTER_AKTOR.increment();
            if (tokenHandler.isExpired()) {
                throw new TokenExpiredException(tokenHandler.getExp(), e);
            }
            throw e;
        }
    }

    private String hentId(AktorId aktørId) {
        try {
            return aktoerV2.hentIdentForAktoerId(request(aktørId)).getIdent();
        } catch (HentIdentForAktoerIdPersonIkkeFunnet e) {
            LOG.warn("Henting av fnr har feilet", e);
            throw new NotFoundException(e);
        } catch (SOAPFaultException e) {
            ERROR_COUNTER_FNR.increment();
            if (tokenHandler.isExpired()) {
                throw new TokenExpiredException(tokenHandler.getExp(), e);
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

    private static Retry retry() {
        return RetryUtil.retry(2, "aktør/fnr", SOAPFaultException.class, LOG);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktoerV2=" + aktoerV2 + ", healthIndicator=" + healthIndicator
                + ", tokenHandler=" + tokenHandler + ", retry=" + retry + "]";
    }

}

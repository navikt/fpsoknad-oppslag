package no.nav.foreldrepenger.oppslag.ws.aktor;

import static no.nav.foreldrepenger.oppslag.config.Constants.NAV_AKTØR_ID;

import java.util.Objects;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.oppslag.error.NotFoundException;
import no.nav.foreldrepenger.oppslag.error.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.util.MDCUtil;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdRequest;

public class AktørIdClientWs implements AktørTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(AktørIdClientWs.class);

    private final AktoerV2 aktoerV2;
    private final AktoerV2 healthIndicator;
    private final TokenUtil tokenUtil;

    public AktørIdClientWs(AktoerV2 aktoerV2, AktoerV2 healthIndicator, TokenUtil tokenUtil) {
        this.aktoerV2 = Objects.requireNonNull(aktoerV2);
        this.healthIndicator = Objects.requireNonNull(healthIndicator);
        this.tokenUtil = tokenUtil;
    }

    @Override
    // @Cacheable(cacheNames = "fnr")
    public AktørId aktorIdForFnr(Fødselsnummer fnr) {
        return new AktørId(hentAktør(fnr));
    }

    @Override
    // @Cacheable(cacheNames = "aktør")
    public Fødselsnummer fnrForAktørId(AktørId aktørId) {
        return new Fødselsnummer(hentId(aktørId));
    }

    @Override
    public void ping() {
        LOG.info("Pinger Aktørregisteret");
        healthIndicator.ping();
    }

    private String hentAktør(Fødselsnummer fnr) {
        LOG.trace("Henter aktør for fnr {}", fnr);
        try {
            String aktørId = aktoerV2.hentAktoerIdForIdent(request(fnr)).getAktoerId();
            MDCUtil.toMDC(NAV_AKTØR_ID, aktørId);
            LOG.trace("Aktørid for {} er {}", fnr, aktørId);
            return aktørId;
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            throw new NotFoundException(fnr.getFnr(), e);
        } catch (WebServiceException e) {
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                throw new NotFoundException(e);
            }
            LOG.trace("Feil ved henting av aktør", e);
            if (tokenUtil.isExpired()) {
                LOG.trace("Token expired: {}", tokenUtil.getExpiryDate());
                throw new TokenExpiredException(tokenUtil.getExpiryDate(), e);
            }
            throw e;
        }
    }

    private String hentId(AktørId aktørId) {
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

    private static HentIdentForAktoerIdRequest request(AktørId aktørId) {
        HentIdentForAktoerIdRequest req = new HentIdentForAktoerIdRequest();
        req.setAktoerId(aktørId.getAktør());
        return req;
    }

    private static HentAktoerIdForIdentRequest request(Fødselsnummer fnr) {
        HentAktoerIdForIdentRequest req = new HentAktoerIdForIdentRequest();
        req.setIdent(fnr.getFnr());
        return req;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktoerV2=" + aktoerV2 + ", healthIndicator=" + healthIndicator
                + ", tokenUtil=" + tokenUtil + "]";
    }
}

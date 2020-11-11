package no.nav.foreldrepenger.oppslag.ws.bankkonto;

import static no.nav.foreldrepenger.oppslag.ws.bankkonto.RequestUtil.request;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.BANKKONTO;

import java.util.Objects;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.oppslag.error.NotFoundException;
import no.nav.foreldrepenger.oppslag.error.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.error.UnauthorizedException;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

public class BankkontoClientWs implements BankkontoTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(BankkontoClientWs.class);
    private final PersonV3 person;
    private final PersonV3 healthIndicator;
    private final TokenUtil tokenUtil;

    public BankkontoClientWs(PersonV3 person, PersonV3 healthIndicator, TokenUtil tokenUtil) {
        this.person = Objects.requireNonNull(person);
        this.healthIndicator = healthIndicator;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void ping() {
        LOG.info("Pinger TPS");
        healthIndicator.ping();
    }

    @Override
    public Bankkonto bankkonto(Fødselsnummer fnr) {
        return BankkontoMapper.kontonr(hentPerson(fnr).getPerson());
    }

    private HentPersonResponse hentPerson(Fødselsnummer fnr) {
        try {
            return person.hentPerson(request(fnr, BANKKONTO));
        } catch (SOAPFaultException e) {
            if (tokenUtil.isExpired()) {
                throw new TokenExpiredException(tokenUtil.getExpiryDate(), e);
            }
            throw e;
        } catch (HentPersonPersonIkkeFunnet e) {
            LOG.warn("Fant ikke person", e);
            throw new NotFoundException(e);
        } catch (HentPersonSikkerhetsbegrensning e) {
            LOG.warn("Sikkerhetsbegrensning ved oppslag", e);
            throw new UnauthorizedException(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [person=" + person + ", healthIndicator=" + healthIndicator
                + ", tokenUtil=" + tokenUtil + "]";
    }

}

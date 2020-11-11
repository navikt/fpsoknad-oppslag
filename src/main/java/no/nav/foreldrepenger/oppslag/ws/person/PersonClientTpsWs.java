package no.nav.foreldrepenger.oppslag.ws.person;

import static no.nav.foreldrepenger.oppslag.ws.person.PersonMapper.person;
import static no.nav.foreldrepenger.oppslag.ws.person.PersonRequestUtil.request;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.BANKKONTO;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.FAMILIERELASJONER;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.KOMMUNIKASJON;

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
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

public class PersonClientTpsWs implements PersonTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(PersonClientTpsWs.class);
    private final PersonV3 person;
    private final PersonV3 healthIndicator;
    private final TokenUtil tokenUtil;

    public PersonClientTpsWs(PersonV3 person, PersonV3 healthIndicator, TokenUtil tokenUtil) {
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
    public Person hentPersonInfo(Fødselsnummer fnr) {
        HentPersonRequest request = request(fnr, KOMMUNIKASJON, BANKKONTO, FAMILIERELASJONER);
        LOG.trace("Slår opp person");
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person tpsPerson = hentPerson(request).getPerson();
        Person p = person(fnr, tpsPerson);
        LOG.trace("Slo opp person OK");
        return p;
    }

    private HentPersonResponse hentPerson(HentPersonRequest request) {
        try {
            return person.hentPerson(request);
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

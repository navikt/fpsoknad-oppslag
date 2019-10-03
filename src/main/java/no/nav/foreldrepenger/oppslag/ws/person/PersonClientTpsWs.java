package no.nav.foreldrepenger.oppslag.ws.person;

import static io.github.resilience4j.retry.Retry.decorateSupplier;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.oppslag.util.EnvUtil.CONFIDENTIAL;
import static no.nav.foreldrepenger.oppslag.util.RetryUtil.DEFAULT_RETRIES;
import static no.nav.foreldrepenger.oppslag.ws.person.PersonMapper.barn;
import static no.nav.foreldrepenger.oppslag.ws.person.PersonMapper.person;
import static no.nav.foreldrepenger.oppslag.ws.person.PersonRequestUtil.BARN;
import static no.nav.foreldrepenger.oppslag.ws.person.PersonRequestUtil.DNR;
import static no.nav.foreldrepenger.oppslag.ws.person.PersonRequestUtil.FAR;
import static no.nav.foreldrepenger.oppslag.ws.person.PersonRequestUtil.FNR;
import static no.nav.foreldrepenger.oppslag.ws.person.PersonRequestUtil.MOR;
import static no.nav.foreldrepenger.oppslag.ws.person.PersonRequestUtil.isFnr;
import static no.nav.foreldrepenger.oppslag.ws.person.PersonRequestUtil.request;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.BANKKONTO;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.FAMILIERELASJONER;
import static no.nav.tjeneste.virksomhet.person.v3.informasjon.Informasjonsbehov.KOMMUNIKASJON;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import io.github.resilience4j.retry.Retry;
import no.nav.foreldrepenger.oppslag.error.NotFoundException;
import no.nav.foreldrepenger.oppslag.error.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.error.UnauthorizedException;
import no.nav.foreldrepenger.oppslag.util.RetryUtil;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Diskresjonskoder;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Doedsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

public class PersonClientTpsWs implements PersonTjeneste {
    private static final String DØDD = "DØDD";
    private static final String DØD = "DØD";
    private static final Logger LOG = LoggerFactory.getLogger(PersonClientTpsWs.class);
    private static final String STRENGT_FORTROLIG_ADRESSE = "SPSF";
    private static final String FORTROLIG_ADRESSE = "SPFO";
    private final PersonV3 person;
    private final PersonV3 healthIndicator;
    private final Barnutvelger barnutvelger;
    private final TokenUtil tokenUtil;
    private final Retry retryConfig;

    PersonClientTpsWs(PersonV3 person, PersonV3 healthIndicator, TokenUtil tokenUtil,
            Barnutvelger barnutvelger) {
        this(person, healthIndicator, tokenUtil, barnutvelger, defaultRetryConfig());
    }

    public PersonClientTpsWs(PersonV3 person, PersonV3 healthIndicator, TokenUtil tokenUtil,
            Barnutvelger barnutvelger, Retry retryConfig) {
        this.person = Objects.requireNonNull(person);
        this.healthIndicator = healthIndicator;
        this.tokenUtil = tokenUtil;
        this.barnutvelger = Objects.requireNonNull(barnutvelger);
        this.retryConfig = retryConfig;
    }

    @Override
    public void ping() {
        LOG.info("Pinger TPS");
        healthIndicator.ping();
    }

    @Override
    @Cacheable(cacheNames = "navn")
    public Navn navn(Fødselsnummer fnr) {
        HentPersonRequest request = request(fnr);
        LOG.info("Slår opp navn");
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person = tpsPersonWithRetry(request);
        return navnFor(person);
    }

    private Navn navnFor(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        return Optional.ofNullable(person)
                .map(p -> PersonMapper.name(p.getPersonnavn(), Kjønn.valueOf(p.getKjoenn().getKjoenn().getValue())))
                .orElse(null);
    }

    @Override
    public Person hentPersonInfo(ID id) {
        HentPersonRequest request = request(id.getFnr(), KOMMUNIKASJON, BANKKONTO, FAMILIERELASJONER);
        LOG.info("Slår opp person");
        LOG.info(CONFIDENTIAL, "Fra ID {}", id);
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person tpsPerson = tpsPersonWithRetry(request);
        Person p = person(id, tpsPerson, barnFor(tpsPerson));
        LOG.info("Slo opp person OK");
        LOG.info(CONFIDENTIAL, "Person er {}", p);
        return p;
    }

    private no.nav.tjeneste.virksomhet.person.v3.informasjon.Person tpsPersonWithRetry(HentPersonRequest request) {
        return decorateSupplier(retryConfig, () -> hentPerson(request)).get().getPerson();
    }

    private List<Barn> barnFor(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        PersonIdent id = (PersonIdent) person.getAktoer();
        String idType = id.getIdent().getType().getValue();
        switch (idType) {
        case FNR:
        case DNR:
            Fødselsnummer fnrSøker = new Fødselsnummer(id.getIdent().getIdent());
            return person.getHarFraRolleI().stream()
                    .filter(this::isBarn)
                    .map(s -> hentBarn(s, fnrSøker))
                    .filter(Objects::nonNull)
                    .filter(barn -> barnutvelger.erStonadsberettigetBarn(fnrSøker, barn))
                    .collect(toList());
        default:
            throw new IllegalStateException("ID type " + idType + " ikke støttet");
        }
    }

    private boolean isBarn(Familierelasjon rel) {
        return rel.getTilRolle().getValue().equals(BARN);
    }

    private boolean isForelder(Familierelasjon rel) {
        String rolle = rel.getTilRolle().getValue();
        return rolle.equals(MOR) || rolle.equals(FAR);
    }

    private Barn hentBarn(Familierelasjon rel, Fødselsnummer fnrSøker) {
        NorskIdent id = ((PersonIdent) rel.getTilPerson().getAktoer()).getIdent();
        if (!isFnr(id)) {
            return null;
        }
        Fødselsnummer fnrBarn = new Fødselsnummer(id.getIdent());
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person tpsBarn = tpsPersonWithRetry(
                request(fnrBarn, FAMILIERELASJONER));
        if (!skalKunneVises(tpsBarn) || harDøddForOverFireMånederSiden(tpsBarn)) {
            return null;
        }

        AnnenForelder annenForelder = tpsBarn.getHarFraRolleI().stream()
                .filter(this::isForelder)
                .map(this::toFødselsnummer)
                .filter(Objects::nonNull)
                .filter(fnr -> !fnr.equals(fnrSøker))
                .map(fnr -> tpsPersonWithRetry(request(fnr)))
                .filter(this::skalKunneVises)
                .filter(p -> !erDød(p))
                .map(PersonMapper::annenForelder)
                .findFirst()
                .orElse(null);
        return barn(id, fnrSøker, tpsBarn, annenForelder);
    }

    private boolean harDøddForOverFireMånederSiden(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        Doedsdato dødsdato = person.getDoedsdato();
        if (dødsdato == null) {
            return false;
        }
        XMLGregorianCalendar dato = dødsdato.getDoedsdato();
        return dato != null
                && LocalDate.of(dato.getYear(), dato.getMonth(), dato.getDay()).isBefore(now().minusMonths(4));
    }

    private boolean erDød(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        try {
            String status = person.getPersonstatus().getPersonstatus().getValue();
            return DØD.equals(status) || DØDD.equals(status);
        } catch (Exception e) {
            LOG.warn("Feil ved sjekking av personstatus", e);
            return false;
        }
    }

    private boolean skalKunneVises(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        Diskresjonskoder diskresjonskode = person.getDiskresjonskode();
        if (diskresjonskode != null) {
            String verdi = diskresjonskode.getValue();
            return verdi != null && !verdi.equals(STRENGT_FORTROLIG_ADRESSE) && !verdi.equals(FORTROLIG_ADRESSE);
        }
        return true;
    }

    private Fødselsnummer toFødselsnummer(Familierelasjon rel) {
        NorskIdent id = ((PersonIdent) rel.getTilPerson().getAktoer()).getIdent();
        if (isFnr(id)) {
            return new Fødselsnummer(id.getIdent());
        }
        return null;
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

    private static Retry defaultRetryConfig() {
        return RetryUtil.retry(DEFAULT_RETRIES, "person", SOAPFaultException.class, LOG);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [person=" + person + ", healthIndicator=" + healthIndicator
                + ", barnutvelger=" + barnutvelger + ", tokenUtil=" + tokenUtil + ", retryConfig=" + retryConfig + "]";
    }
}

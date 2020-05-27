package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.ws;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.oppslag.error.IncompleteRequestException;
import no.nav.foreldrepenger.oppslag.error.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.error.UnauthorizedException;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.ArbeidsforholdTjeneste;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;

public class ArbeidsforholdClientWs implements ArbeidsforholdTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdClientWs.class);
    private final ArbeidsforholdV3 arbeidsforholdV3;
    private final ArbeidsforholdV3 healthIndicator;
    private final OrganisasjonClient orgClient;
    private final TokenUtil tokenUtil;

    public ArbeidsforholdClientWs(ArbeidsforholdV3 arbeidsforholdV3, ArbeidsforholdV3 healthIndicator,
            OrganisasjonClient orgClient, TokenUtil tokenUtil) {
        this.arbeidsforholdV3 = arbeidsforholdV3;
        this.healthIndicator = healthIndicator;
        this.orgClient = orgClient;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void ping() {
        try {
            LOG.info("Pinger AAreg");
            healthIndicator.ping();
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<Arbeidsforhold> aktiveArbeidsforhold() {
        return aktiveArbeidsforhold(new Fødselsnummer(tokenUtil.autentisertBruker()));
    }

    @Override
    public List<Arbeidsforhold> aktiveArbeidsforhold(Fødselsnummer fnr) {
        List<Arbeidsforhold> arbeidsforhold = arbeidsforholdSiste3år(fnr.getFnr());
        for (Arbeidsforhold forhold : arbeidsforhold) {
            Optional<String> navn = navnFor(forhold.getArbeidsgiverId());
            navn.ifPresent(forhold::setArbeidsgiverNavn);
        }
        LOG.trace("Fant {} aktive arbeidsforhold ({})", arbeidsforhold.size(), arbeidsforhold);
        return arbeidsforhold;
    }

    private Optional<String> navnFor(String orgnr) {
        try {
            return nameFor(orgnr);
        } catch (SOAPFaultException e) {
            return Optional.empty();
        }
    }

    private List<Arbeidsforhold> arbeidsforholdSiste3år(String fnr) {
        try {
            FinnArbeidsforholdPrArbeidstakerResponse response = arbeidsforholdV3
                    .finnArbeidsforholdPrArbeidstaker(request(fnr));
            return response.getArbeidsforhold().stream()
                    .map(ArbeidsforholdMapper::map)
                    .filter(this::siste3år)
                    .collect(toList());
        } catch (FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning e) {
            LOG.warn("Sikkerhetsfeil fra AAREG", e);
            throw new UnauthorizedException(e);
        } catch (FinnArbeidsforholdPrArbeidstakerUgyldigInput e) {
            throw new IncompleteRequestException(e);
        } catch (SOAPFaultException e) {
            if (tokenUtil.isExpired()) {
                throw new TokenExpiredException(tokenUtil.getExpiryDate(), e);
            }
            throw e;
        }
    }

    private static FinnArbeidsforholdPrArbeidstakerRequest request(String fnr) {
        FinnArbeidsforholdPrArbeidstakerRequest request = new FinnArbeidsforholdPrArbeidstakerRequest();
        NorskIdent ident = new NorskIdent();
        ident.setIdent(fnr);
        request.setIdent(ident);
        Regelverker regelverker = new Regelverker();
        regelverker.setValue("ALLE");
        request.setRapportertSomRegelverk(regelverker);
        return request;
    }

    private Optional<String> nameFor(String orgnr) {
        return orgClient.nameFor(orgnr);
    }

    boolean siste3år(Arbeidsforhold arbeidsforhold) {
        LOG.trace("Sjekker om {} er aktivt siste 3 år", arbeidsforhold);
        if (arbeidsforhold.getTom().isPresent()) {
            LocalDate tom = arbeidsforhold.getTom().get();
            boolean aktiv = tom.isAfter(LocalDate.now().minusYears(3));
            LOG.trace("Arbeidsforhold  {} er {} aktivt siste 3 år{}", arbeidsforhold, aktiv ? "" : "ikke",
                    !aktiv ? ", ble avsluttet " + tom : "");
            return aktiv;
        }
        LOG.trace("Ingen sluttdato for {}, antar aktivt", arbeidsforhold);
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [arbeidsforholdV3=" + arbeidsforholdV3 + ", healthIndicator="
                + healthIndicator + ", orgClient=" + orgClient + ", tokenUtil=" + tokenUtil + "]";
    }

    @Override
    public String arbeidsgiverNavn(String orgnr) {
        Optional<String> navn = navnFor(orgnr);
        return navn.isPresent() ? navn.get() : null;
    }
}

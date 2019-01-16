package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import static io.github.resilience4j.retry.Retry.decorateSupplier;
import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.lookup.util.RetryUtil.DEFAULT_RETRIES;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.resilience4j.retry.Retry;
import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.errorhandling.IncompleteRequestException;
import no.nav.foreldrepenger.errorhandling.TokenExpiredException;
import no.nav.foreldrepenger.errorhandling.UnauthorizedException;
import no.nav.foreldrepenger.lookup.util.RetryUtil;
import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerUgyldigInput;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.NorskIdent;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Regelverker;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerRequest;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;

public class ArbeidsforholdClientWs implements ArbeidsforholdClient {

    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdClientWs.class);

    private final ArbeidsforholdV3 arbeidsforholdV3;
    private final ArbeidsforholdV3 healthIndicator;
    private final OrganisasjonClient orgClient;
    private final TokenUtil tokenHandler;
    private final Retry retry;

    ArbeidsforholdClientWs(ArbeidsforholdV3 arbeidsforholdV3, ArbeidsforholdV3 healthIndicator,
            OrganisasjonClient orgClient, TokenUtil tokenHandler) {
        this(arbeidsforholdV3, healthIndicator, orgClient, tokenHandler, retry());
    }

    public ArbeidsforholdClientWs(ArbeidsforholdV3 arbeidsforholdV3, ArbeidsforholdV3 healthIndicator,
            OrganisasjonClient orgClient, TokenUtil tokenHandler, Retry retry) {
        this.arbeidsforholdV3 = arbeidsforholdV3;
        this.healthIndicator = healthIndicator;
        this.orgClient = orgClient;
        this.tokenHandler = tokenHandler;
        this.retry = retry;
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
    @Timed("lookup.arbeidsforhold")
    public List<Arbeidsforhold> aktiveArbeidsforhold(Fødselsnummer fnr) {
        List<Arbeidsforhold> arbeidsforhold = decorateSupplier(retry, () -> aktiveArbeidsforhold(fnr.getFnr())).get();
        for (Arbeidsforhold forhold : arbeidsforhold) {
            Optional<String> navn = navn(forhold.getArbeidsgiverId());
            if (navn.isPresent()) {
                forhold.setArbeidsgiverNavn(navn.get());
            }
        }
        return arbeidsforhold;
    }

    private Optional<String> navn(String orgnr) {
        try {
            return arbeidsgiverNavn(orgnr);
        } catch (SOAPFaultException e) {
            return Optional.empty();
        }
    }

    private List<Arbeidsforhold> aktiveArbeidsforhold(String fnr) {
        try {
            FinnArbeidsforholdPrArbeidstakerResponse response = arbeidsforholdV3
                    .finnArbeidsforholdPrArbeidstaker(request(fnr));
            return response.getArbeidsforhold().stream()
                    .map(ArbeidsforholdMapper::map)
                    .filter(this::isOngoing)
                    .collect(toList());
        } catch (FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning e) {
            LOG.warn("Sikkerhetsfeil fra AAREG", e);
            throw new UnauthorizedException(e);
        } catch (FinnArbeidsforholdPrArbeidstakerUgyldigInput e) {
            throw new IncompleteRequestException(e);
        } catch (SOAPFaultException e) {
            if (tokenHandler.isExpired()) {
                throw new TokenExpiredException(tokenHandler.getExp(), e);
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

    private Optional<String> arbeidsgiverNavn(String orgnr) {
        return orgClient.nameFor(orgnr);
    }

    private static Retry retry() {
        return RetryUtil.retry(DEFAULT_RETRIES, "arbeidforhold", SOAPFaultException.class, LOG);
    }

    boolean isOngoing(Arbeidsforhold arbeidsforhold) {
        LocalDate today = LocalDate.now();
        return arbeidsforhold.getTom()
                .map(t -> t.isAfter(today) || t.equals(today))
                .orElse(true);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [arbeidsforholdV3=" + arbeidsforholdV3 + ", healthIndicator="
                + healthIndicator + ", orgClient=" + orgClient + ", tokenHandler=" + tokenHandler + ", retry=" + retry
                + "]";
    }

}

package no.nav.foreldrepenger.oppslag.inntekt;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Inntekt;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.IncompleteRequestException;
import no.nav.foreldrepenger.oppslag.time.CalendarConverter;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.HentInntektListeHarIkkeTilgangTilOensketAInntektsfilter;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.HentInntektListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.HentInntektListeUgyldigInput;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Ainntektsfilter;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Formaal;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.PersonIdent;
import no.nav.tjeneste.virksomhet.inntekt.v3.informasjon.inntekt.Uttrekksperiode;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeRequest;
import no.nav.tjeneste.virksomhet.inntekt.v3.meldinger.HentInntektListeResponse;

public class InntektClientWs implements InntektClient {
    private static final Logger log = LoggerFactory.getLogger(InntektClientWs.class);

    private final InntektV3 inntektV3;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.inntekt");

    @Inject
    public InntektClientWs(InntektV3 inntektV3) {
        this.inntektV3 = inntektV3;
    }

    public void ping() {
        inntektV3.ping();
    }

    public List<Inntekt> incomeForPeriod(Fodselsnummer fnr, LocalDate from, LocalDate to) {
        HentInntektListeRequest req = request(fnr, from, to);
        try {
            HentInntektListeResponse res = inntektV3.hentInntektListe(req);
            return res.getArbeidsInntektIdent().getArbeidsInntektMaaned().stream()
                .flatMap(aim -> aim.getArbeidsInntektInformasjon().getInntektListe().stream())
                .map(InntektMapper::map).collect(toList());
        } catch (HentInntektListeHarIkkeTilgangTilOensketAInntektsfilter | HentInntektListeSikkerhetsbegrensning e) {
            log.warn("Error while retrieving income", e);
            throw new ForbiddenException(e);
        } catch (HentInntektListeUgyldigInput e) {
            log.warn("Error while retrieving income", e);
            throw new IncompleteRequestException(e);
        } catch (Exception ex) {
            ERROR_COUNTER.increment();
            throw new RuntimeException(ex);
        }
    }

    private HentInntektListeRequest request(Fodselsnummer fnr, LocalDate from, LocalDate to) {
        HentInntektListeRequest req = new HentInntektListeRequest();

        PersonIdent person = new PersonIdent();
        person.setPersonIdent(fnr.getFnr());
        req.setIdent(person);

        Ainntektsfilter ainntektsfilter = new Ainntektsfilter();
        ainntektsfilter.setValue("ForeldrepengerA-Inntekt");
        ainntektsfilter.setKodeRef("ForeldrepengerA-Inntekt");
        ainntektsfilter.setKodeverksRef(
            "http://nav.no/kodeverk/Term/A-inntektsfilter/ForeldrepengerA-Inntekt/nb/Foreldrepenger_20a-inntekt?v=6");
        req.setAinntektsfilter(ainntektsfilter);

        Uttrekksperiode uttrekksperiode = new Uttrekksperiode();
        uttrekksperiode.setMaanedFom(CalendarConverter.toXMLGregorianCalendar(from));
        uttrekksperiode.setMaanedTom(CalendarConverter.toXMLGregorianCalendar(to));
        req.setUttrekksperiode(uttrekksperiode);

        Formaal formaal = new Formaal();
        formaal.setValue("Foreldrepenger");
        formaal.setKodeRef("Foreldrepenger");
        formaal.setKodeverksRef(
            "http://nav.no/kodeverk/Term/A-inntektsfilter/ForeldrepengerA-Inntekt/nb/Foreldrepenger_20a-inntekt?v=6");
        req.setFormaal(formaal);

        return req;
    }
}

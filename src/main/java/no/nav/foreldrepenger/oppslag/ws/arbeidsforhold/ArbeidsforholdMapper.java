package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import static java.time.LocalDate.now;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.oppslag.util.DateUtil;
import no.nav.foreldrepenger.oppslag.util.Pair;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Aktoer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsavtale;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.HistoriskArbeidsgiverMedArbeidsgivernummer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Person;

public final class ArbeidsforholdMapper {

    private ArbeidsforholdMapper() {

    }

    public static Arbeidsforhold map(
            no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold forhold) {
        return new Arbeidsforhold(
                arbeidsgiverIdOgType(forhold.getArbeidsgiver()).getFirst(),
                arbeidsgiverIdOgType(forhold.getArbeidsgiver()).getSecond(),
                stillingsprosent(forhold.getArbeidsavtale()),
                DateUtil.toLocalDate(forhold.getAnsettelsesPeriode().getPeriode().getFom()),
                Optional.ofNullable(forhold.getAnsettelsesPeriode().getPeriode().getTom()).map(DateUtil::toLocalDate));
    }

    private static Pair<String, String> arbeidsgiverIdOgType(Aktoer aktor) {
        if (aktor instanceof Organisasjon) {
            Organisasjon org = (Organisasjon) aktor;
            return Pair.of(org.getOrgnummer(), "orgnr");
        }
        if (aktor instanceof HistoriskArbeidsgiverMedArbeidsgivernummer) {
            HistoriskArbeidsgiverMedArbeidsgivernummer h = (HistoriskArbeidsgiverMedArbeidsgivernummer) aktor;
            return Pair.of(h.getArbeidsgivernummer(), "arbeidsgivernr");
        }
        Person person = (Person) aktor;
        return Pair.of(person.getIdent().getIdent(), "fnr");
    }

    private static Double stillingsprosent(List<Arbeidsavtale> avtaler) {
        return avtaler.stream()
                .filter(ArbeidsforholdMapper::gjeldendeAvtale)
                .map(Arbeidsavtale::getStillingsprosent)
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .findFirst().orElse(null);
    }

    private static boolean gjeldendeAvtale(Arbeidsavtale avtale) {
        LocalDate fom = DateUtil.toLocalDate(avtale.getFomGyldighetsperiode());
        LocalDate tom;
        if (avtale.getTomGyldighetsperiode() != null) {
            tom = DateUtil.toLocalDate(avtale.getTomGyldighetsperiode());
        }
        else {
            tom = now();
        }

        return DateUtil.dateWithinPeriod(now(), fom, tom);
    }

}

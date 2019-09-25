package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import no.nav.foreldrepenger.oppslag.util.DateUtil;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.*;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestdataProvider {

    public static Arbeidsforhold forhold(Aktoer aktoer) {
        Arbeidsforhold forhold = new Arbeidsforhold();

        forhold.setArbeidsgiver(aktoer);

        Gyldighetsperiode gyldighetsperiode = new Gyldighetsperiode();
        LocalDate now = LocalDate.now();
        LocalDate earlier = now.minusMonths(2);
        gyldighetsperiode.setFom(DateUtil.toXMLGregorianCalendar(earlier));
        gyldighetsperiode.setTom(DateUtil.toXMLGregorianCalendar(now));

        AnsettelsesPeriode ansettelsesperiode = new AnsettelsesPeriode();
        ansettelsesperiode.setPeriode(gyldighetsperiode);
        forhold.setAnsettelsesPeriode(ansettelsesperiode);

        Arbeidsforholdstyper type = new Arbeidsforholdstyper();
        type.setValue("typen");
        forhold.setArbeidsforholdstype(type);

        Arbeidsavtale avtale1 = new Arbeidsavtale();
        avtale1.setStillingsprosent(BigDecimal.valueOf(100d));
        avtale1.setFomGyldighetsperiode(DateUtil.toXMLGregorianCalendar(now.minusMonths(6)));
        avtale1.setTomGyldighetsperiode(DateUtil.toXMLGregorianCalendar(now.minusMonths(2)));
        forhold.getArbeidsavtale().add(avtale1);

        Arbeidsavtale avtale2 = new Arbeidsavtale();
        avtale2.setFomGyldighetsperiode(DateUtil.toXMLGregorianCalendar(now.minusMonths(2)));
        forhold.getArbeidsavtale().add(avtale2);

        return forhold;
    }

}

package no.nav.foreldrepenger.oppslag.inntekt;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Inntekt;

import java.time.LocalDate;
import java.util.List;

public interface InntektClient {

    void ping();

    List<Inntekt> incomeForPeriod(Fodselsnummer fnr, LocalDate from, LocalDate to);
}

package no.nav.foreldrepenger.oppslag.infotrygd;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;

import java.time.LocalDate;
import java.util.List;

public interface InfotrygdClient {

    void ping();

    List<Ytelse> casesFor(Fodselsnummer fnr, LocalDate from, LocalDate to);
}

package no.nav.foreldrepenger.oppslag.arena;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;

import java.time.LocalDate;
import java.util.List;

public interface ArenaClient {

    void ping();

    List<Ytelse> ytelser(Fodselsnummer fnr, LocalDate from, LocalDate to);
}

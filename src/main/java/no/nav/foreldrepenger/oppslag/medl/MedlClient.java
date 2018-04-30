package no.nav.foreldrepenger.oppslag.medl;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.MedlPeriode;

import java.util.List;

public interface MedlClient {

    void ping();

    List<MedlPeriode> medlInfo(Fodselsnummer fnr);
}

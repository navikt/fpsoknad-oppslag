package no.nav.foreldrepenger.oppslag.aareg;

import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;

import java.util.List;

public interface AaregClient {

    void ping();

    List<Arbeidsforhold> arbeidsforhold(Fodselsnummer fnr);
}

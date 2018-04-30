package no.nav.foreldrepenger.oppslag.aktor;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;

public interface AktorIdClient {

    AktorId aktorIdForFnr(Fodselsnummer fnr);

    void ping();

}

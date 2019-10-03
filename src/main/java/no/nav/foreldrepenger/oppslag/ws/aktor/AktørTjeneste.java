package no.nav.foreldrepenger.oppslag.ws.aktor;

import no.nav.foreldrepenger.oppslag.util.Pingable;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;

public interface AktørTjeneste extends Pingable {

    AktørId aktorIdForFnr(Fødselsnummer fnr);

    Fødselsnummer fnrForAktørId(AktørId fnr);

}

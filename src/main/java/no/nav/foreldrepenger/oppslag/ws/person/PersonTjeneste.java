package no.nav.foreldrepenger.oppslag.ws.person;

import no.nav.foreldrepenger.oppslag.util.Pingable;

public interface PersonTjeneste extends Pingable {

    Person hentPersonInfo(ID id);

    Navn navn(Fødselsnummer fnr);

}

package no.nav.foreldrepenger.lookup.ws.person;

import no.nav.foreldrepenger.lookup.Pingable;

public interface PersonClient extends Pingable {

    Person hentPersonInfo(ID id);

    Navn navn(Fødselsnummer fnr);

}

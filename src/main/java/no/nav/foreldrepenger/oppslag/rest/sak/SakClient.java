package no.nav.foreldrepenger.oppslag.rest.sak;

import java.util.List;

import no.nav.foreldrepenger.oppslag.ws.aktor.AktørId;

public interface SakClient {

    List<Sak> sakerFor(AktørId aktor, String tema);

}

package no.nav.foreldrepenger.oppslag.rest.sak;

import java.util.List;

import no.nav.foreldrepenger.oppslag.ws.aktor.AktorId;

public interface SakClient {

    List<Sak> sakerFor(AktorId aktor, String tema);

}

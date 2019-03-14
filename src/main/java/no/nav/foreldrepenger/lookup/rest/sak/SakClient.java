package no.nav.foreldrepenger.lookup.rest.sak;

import java.util.List;

import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;

public interface SakClient {

    List<Sak> sakerFor(AktorId aktor, String tema);

}

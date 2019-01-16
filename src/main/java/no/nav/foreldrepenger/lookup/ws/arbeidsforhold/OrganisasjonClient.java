package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import java.util.Optional;

import no.nav.foreldrepenger.lookup.Pingable;

public interface OrganisasjonClient extends Pingable {

    Optional<String> nameFor(String orgnr);

}

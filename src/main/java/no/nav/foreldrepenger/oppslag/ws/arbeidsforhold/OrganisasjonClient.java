package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.util.Pingable;

public interface OrganisasjonClient extends Pingable {

    Optional<String> nameFor(String orgnr);

}

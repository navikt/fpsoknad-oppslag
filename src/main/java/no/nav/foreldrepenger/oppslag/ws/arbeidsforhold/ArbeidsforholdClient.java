package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import java.util.List;

import no.nav.foreldrepenger.oppslag.util.Pingable;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;

public interface ArbeidsforholdClient extends Pingable {
    List<Arbeidsforhold> aktiveArbeidsforhold(Fødselsnummer fnr);

    String arbeidsgiverNavn(String orgnr);
}

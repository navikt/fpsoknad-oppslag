package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import org.springframework.retry.annotation.Retryable;

import no.nav.foreldrepenger.oppslag.error.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.util.Pingable;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;

@Retryable(include = SOAPFaultException.class, exclude = TokenExpiredException.class)
public interface ArbeidsforholdTjeneste extends Pingable {
    List<Arbeidsforhold> aktiveArbeidsforhold(Fødselsnummer fnr);

    String arbeidsgiverNavn(String orgnr);
}

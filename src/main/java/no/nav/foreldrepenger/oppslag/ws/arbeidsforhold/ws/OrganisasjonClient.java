package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.ws;

import java.util.Optional;

import javax.xml.ws.soap.SOAPFaultException;

import org.springframework.retry.annotation.Retryable;

import no.nav.foreldrepenger.oppslag.error.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.util.Pingable;

@Retryable(include = SOAPFaultException.class, exclude = TokenExpiredException.class)
public interface OrganisasjonClient extends Pingable {

    Optional<String> nameFor(String orgnr);

}

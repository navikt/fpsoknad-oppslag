package no.nav.foreldrepenger.oppslag.ws.aktor;

import javax.xml.ws.WebServiceException;

import org.springframework.retry.annotation.Retryable;

import no.nav.foreldrepenger.oppslag.error.NotFoundException;
import no.nav.foreldrepenger.oppslag.error.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.util.Pingable;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;

@Retryable(maxAttempts = 2, include = WebServiceException.class, exclude = { TokenExpiredException.class,
        NotFoundException.class })
public interface AktørTjeneste extends Pingable {
    AktørId aktorIdForFnr(Fødselsnummer fnr);

    Fødselsnummer fnrForAktørId(AktørId fnr);
}

package no.nav.foreldrepenger.oppslag.ws.person;

import javax.xml.ws.soap.SOAPFaultException;

import org.springframework.retry.annotation.Retryable;

import no.nav.foreldrepenger.oppslag.error.NotFoundException;
import no.nav.foreldrepenger.oppslag.error.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.error.UnauthorizedException;
import no.nav.foreldrepenger.oppslag.util.Pingable;

@Retryable(include = SOAPFaultException.class, exclude = { UnauthorizedException.class, NotFoundException.class,
        TokenExpiredException.class })
public interface PersonTjeneste extends Pingable {

    Person hentPersonInfo(ID id);

    Navn navn(Fødselsnummer fnr);

}

package no.nav.foreldrepenger.oppslag.ws.bankkonto;

import javax.xml.ws.WebServiceException;

import org.springframework.retry.annotation.Retryable;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.error.NotFoundException;
import no.nav.foreldrepenger.oppslag.error.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.error.UnauthorizedException;
import no.nav.foreldrepenger.oppslag.http.Pingable;

@Retryable(
    include = WebServiceException.class,
    exclude = {
        UnauthorizedException.class,
        NotFoundException.class,
        TokenExpiredException.class
    }
)
public interface BankkontoTjeneste extends Pingable {

    Bankkonto bankkonto(Fødselsnummer fnr);

}

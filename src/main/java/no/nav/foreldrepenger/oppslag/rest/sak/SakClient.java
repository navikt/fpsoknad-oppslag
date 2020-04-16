package no.nav.foreldrepenger.oppslag.rest.sak;

import java.util.List;

import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpServerErrorException;

import no.nav.foreldrepenger.oppslag.ws.aktor.AktørId;

@Retryable(include = HttpServerErrorException.class, maxAttempts = 2)
public interface SakClient {
    List<Sak> sakerFor(AktørId aktor, String tema);
}

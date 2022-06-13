package no.nav.foreldrepenger.oppslag.error;

import java.time.LocalDateTime;

public class TokenExpiredException extends UnauthenticatedException {

    public TokenExpiredException(LocalDateTime expDate, Throwable cause) {
        super(expDate, cause);
    }

}

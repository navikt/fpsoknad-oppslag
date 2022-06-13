package no.nav.foreldrepenger.oppslag.error;

import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

import java.time.LocalDateTime;

public class UnauthenticatedException extends RuntimeException {

    private final LocalDateTime expDate;

    public UnauthenticatedException(String msg) {
        this(msg, null, null);
    }

    public UnauthenticatedException(LocalDateTime expDate, Throwable cause) {
        this(cause != null ? getMostSpecificCause(cause).getMessage() : null, expDate, cause);
    }

    public UnauthenticatedException(String msg, LocalDateTime expDate, Throwable cause) {
        super(msg, cause);
        this.expDate = expDate;
    }

    public LocalDateTime getExpiryDate() {
        return expDate;
    }

}

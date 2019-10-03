package no.nav.foreldrepenger.oppslag.error;

import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

import java.util.Date;

public class UnauthenticatedException extends RuntimeException {

    private final Date expDate;

    public UnauthenticatedException(String msg) {
        this(msg, null, null);
    }

    public UnauthenticatedException(Date expDate, Throwable cause) {
        this(cause != null ? getMostSpecificCause(cause).getMessage() : null, expDate, cause);
    }

    public UnauthenticatedException(String msg, Date expDate, Throwable cause) {
        super(msg, cause);
        this.expDate = expDate;
    }

    public Date getExpiryDate() {
        return expDate;
    }

}

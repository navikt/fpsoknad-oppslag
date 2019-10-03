package no.nav.foreldrepenger.oppslag.error;

import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;

import java.util.Date;

public class UnauthorizedException extends RuntimeException {

    private final Date expDate;

    public UnauthorizedException(String msg) {
        this(msg, null, null);
    }

    public UnauthorizedException(Throwable cause) {
        this(null, null, cause);

    }

    public UnauthorizedException(Date expDate, Throwable cause) {
        this(cause != null ? getMostSpecificCause(cause).getMessage() : null, expDate, cause);
    }

    public UnauthorizedException(String msg, Date expDate, Throwable cause) {
        super(msg, cause);
        this.expDate = expDate;
    }

    public Date getExpiryDate() {
        return expDate;
    }

}

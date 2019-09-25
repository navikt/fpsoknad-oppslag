package no.nav.foreldrepenger.oppslag.errorhandling;

public class IncompleteRequestException extends RuntimeException {

    public IncompleteRequestException(Throwable cause) {
        this(null, cause);
    }

    public IncompleteRequestException(String msg) {
        this(msg, null);
    }

    public IncompleteRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }

}

package no.nav.foreldrepenger.oppslag.ws.bankkonto;

import java.util.Objects;

import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.oppslag.util.StringUtil;

@Validated
public class Fødselsnummer {

    @Size(min = 11, max = 11)
    private final String fnr;

    public Fødselsnummer(String fnr) {
        this.fnr = Objects.requireNonNull(fnr);
    }

    @JsonValue
    public String getFnr() {
        return fnr;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fnr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        Fødselsnummer that = (Fødselsnummer) o;
        return Objects.equals(fnr, that.fnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + StringUtil.mask(fnr) + "]";
    }

}

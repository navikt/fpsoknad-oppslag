package no.nav.foreldrepenger.oppslag.ws.person;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.oppslag.ws.aktor.AktorId;

public class ID {

    private final AktorId aktorId;
    private final Fødselsnummer fnr;

    @JsonCreator
    public ID(@JsonProperty("aktorId") AktorId aktorId, @JsonProperty("fnr") Fødselsnummer fnr) {
        this.aktorId = aktorId;
        this.fnr = fnr;
    }

    public AktorId getAktorId() {
        return aktorId;
    }

    public Fødselsnummer getFnr() {
        return fnr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ID id = (ID) o;
        return Objects.equals(aktorId, id.aktorId) &&
                Objects.equals(fnr, id.fnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktorId, fnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktorId=" + aktorId + ", fnr=" + fnr + "]";
    }
}

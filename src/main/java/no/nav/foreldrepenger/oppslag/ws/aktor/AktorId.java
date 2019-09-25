package no.nav.foreldrepenger.oppslag.ws.aktor;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonValue;

public class AktorId {

    private final String aktør;

    @NotNull
    public AktorId(String aktør) {
        this.aktør = Objects.requireNonNull(aktør);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(aktør);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        AktorId that = (AktorId) o;
        return Objects.equals(aktør, that.aktør);
    }

    @JsonValue
    public String getAktør() {
        return aktør;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [aktør=" + aktør + "]";
    }

}

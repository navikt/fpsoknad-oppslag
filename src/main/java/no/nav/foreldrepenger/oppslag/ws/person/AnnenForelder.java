package no.nav.foreldrepenger.oppslag.ws.person;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class AnnenForelder {

    private final Navn navn;
    private final Fødselsnummer fnr;
    private final LocalDate fødselsdato;

    public AnnenForelder(Navn navn, Fødselsnummer fnr, LocalDate fødselsdato) {
        this.navn = navn;
        this.fnr = fnr;
        this.fødselsdato = fødselsdato;
    }

    @JsonUnwrapped
    public Navn getNavn() {
        return navn;
    }

    @JsonUnwrapped
    public Fødselsnummer getFnr() {
        return fnr;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        AnnenForelder that = (AnnenForelder) o;
        return Objects.equals(fnr, that.fnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fnr);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [navn=" + navn + ", fnr=" + fnr + ", fødselsdato=" + fødselsdato + "]";
    }
}

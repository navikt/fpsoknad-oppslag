package no.nav.foreldrepenger.lookup.ws.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class Barn {
    private final Fødselsnummer fnr;
    private final Fødselsnummer fnrSøker;
    private final LocalDate fødselsdato;
    private final Navn navn;
    private final AnnenForelder annenForelder;

    public Barn(Fødselsnummer fnrSøker, Fødselsnummer fnr, LocalDate fødselsdato, Navn navn,
            AnnenForelder annenForelder) {
        this.fnr = fnr;
        this.fnrSøker = fnrSøker;
        this.fødselsdato = requireNonNull(fødselsdato);
        this.navn = navn;
        this.annenForelder = annenForelder;
    }

    @JsonUnwrapped
    public Fødselsnummer getFnr() {
        return fnr;
    }

    @JsonUnwrapped
    public Fødselsnummer getFnrSøker() {
        return fnrSøker;
    }

    @JsonUnwrapped
    public Navn getNavn() {
        return navn;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public Kjønn getKjønn() {
        return navn.getKjønn();
    }

    public AnnenForelder getAnnenForelder() {
        return annenForelder;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fnr, fødselsdato);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        Barn that = (Barn) o;
        return Objects.equals(fnr, that.fnr) && Objects.equals(fødselsdato, that.fødselsdato);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + fnr + ", fnrSøker=" + fnrSøker + ", fødselsdato=" + fødselsdato
                + ", navn=" + navn + ", annenForelder=" + annenForelder + "]";
    }
}

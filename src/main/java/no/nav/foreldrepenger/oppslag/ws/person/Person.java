package no.nav.foreldrepenger.oppslag.ws.person;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.neovisionaries.i18n.CountryCode;

@JsonPropertyOrder({ "id", "fødselsdato", "navn", "kjønn", "målform" })
public class Person {

    private final Fødselsnummer fnr;
    private final CountryCode landKode;
    private final Kjønn kjønn;
    private final LocalDate fødselsdato;
    private final String målform;
    private final Bankkonto bankkonto;
    private final Navn navn;

    public Person(Fødselsnummer fnr, CountryCode landKode, Kjønn kjønn, Navn navn, String målform,
            Bankkonto bankkonto, LocalDate fødselsdato) {
        this.fnr = fnr;
        this.landKode = landKode;
        this.kjønn = kjønn;
        this.målform = målform;
        this.bankkonto = bankkonto;
        this.navn = navn;
        this.fødselsdato = fødselsdato;
    }

    public Kjønn getKjønn() {
        return kjønn;
    }

    public Fødselsnummer getFnr() {
        return fnr;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public CountryCode getLandKode() {
        return landKode;
    }

    public String getMålform() {
        return målform;
    }

    public Bankkonto getBankkonto() {
        return bankkonto;
    }

    public Navn getNavn() {
        return navn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        Person person = (Person) o;
        return Objects.equals(fnr, person.fnr) &&
                (landKode == person.landKode) &&
                (kjønn == person.kjønn) &&
                Objects.equals(fødselsdato, person.fødselsdato) &&
                Objects.equals(målform, person.målform) &&
                Objects.equals(bankkonto, person.bankkonto) &&
                Objects.equals(navn, person.navn) &&
                Objects.equals(landKode, person.landKode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fnr, landKode, kjønn, fødselsdato, målform, bankkonto, navn);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [fnr=" + fnr + ", landKode=" + landKode + ", kjønn=" + kjønn + ", fødselsdato=" + fødselsdato
                + ", målform=" + målform
                + ", bankkonto=" + bankkonto + ", navn=" + navn + "]";
    }

}

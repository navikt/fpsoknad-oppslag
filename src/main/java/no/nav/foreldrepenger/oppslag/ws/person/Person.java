package no.nav.foreldrepenger.oppslag.ws.person;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.neovisionaries.i18n.CountryCode;

@JsonPropertyOrder({ "id", "fødselsdato", "navn", "kjønn", "målform" })
public class Person {

    @JsonUnwrapped
    private final ID id;
    private final CountryCode landKode;
    private final Kjønn kjønn;
    private final LocalDate fødselsdato;
    private final String målform;
    private final Bankkonto bankkonto;
    private final Navn navn;
    private final List<Barn> barn;

    public Person(ID id, CountryCode landKode, Kjønn kjønn, Navn navn, String målform,
            Bankkonto bankkonto, LocalDate fødselsdato, List<Barn> barn) {
        this.id = id;
        this.landKode = landKode;
        this.kjønn = kjønn;
        this.målform = målform;
        this.bankkonto = bankkonto;
        this.navn = navn;
        this.fødselsdato = fødselsdato;
        this.barn = barn;
    }

    public Kjønn getKjønn() {
        return kjønn;
    }

    public ID getId() {
        return id;
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

    public List<Barn> getBarn() {
        return barn;
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
        return Objects.equals(id, person.id) &&
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
        return Objects.hash(id, landKode, kjønn, fødselsdato, målform, bankkonto, navn);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [id=" + id + ", landKode=" + landKode + ", kjønn=" + kjønn + ", fødselsdato=" + fødselsdato
                + ", målform=" + målform
                + ", bankkonto=" + bankkonto + ", navn=" + navn + ", barn=" + barn + "]";
    }

}

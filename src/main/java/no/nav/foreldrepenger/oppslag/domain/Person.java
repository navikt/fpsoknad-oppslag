package no.nav.foreldrepenger.oppslag.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.oppslag.person.EØSLandVelger;

@JsonPropertyOrder({ "id", "fodselsdato", "navn", "kjonn", "adresse", "målform" })
public class Person {

    @JsonUnwrapped
    private final ID id;
    private final CountryCode landKode;
    private final Kjonn kjonn;
    private final LocalDate fodselsdato;
    private final Adresse adresse;
    private final String målform;
    @JsonUnwrapped
    private final Navn navn;
    private final List<Barn> barn;
    private final boolean isEøs;

    public Person(ID id, CountryCode landKode, Kjonn kjonn, Navn navn, Adresse adresse, String målform,
            LocalDate fodselsdato, List<Barn> barn) {
        this.id = id;
        this.landKode = landKode;
        this.kjonn = kjonn;
        this.adresse = adresse;
        this.målform = målform;
        this.navn = navn;
        this.fodselsdato = fodselsdato;
        this.barn = barn;
        this.isEøs = EØSLandVelger.erAnnetEØSLand(landKode);
    }

    public boolean isEøs() {
        return isEøs;
    }

    public Kjonn getKjonn() {
        return kjonn;
    }

    public ID getId() {
        return id;
    }

    public LocalDate getFodselsdato() {
        return fodselsdato;
    }

    public CountryCode getLandKode() {
        return landKode;
    }

    public List<Barn> getBarn() {
        return barn;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public String getMålform() {
        return målform;
    }

    public Navn getNavn() {
        return navn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                landKode == person.landKode &&
                kjonn == person.kjonn &&
                Objects.equals(fodselsdato, person.fodselsdato) &&
                Objects.equals(adresse, person.adresse) &&
                Objects.equals(målform, person.målform) &&
                Objects.equals(navn, person.navn) &&
                Objects.equals(barn, person.barn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, landKode, kjonn, fodselsdato, adresse, målform, navn, barn);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", landKode=" + landKode +
                ", kjonn=" + kjonn +
                ", fodselsdato=" + fodselsdato +
                ", adresse=" + adresse +
                ", målform='" + målform + '\'' +
                ", navn=" + navn +
                ", barn=" + barn +
                '}';
    }
}

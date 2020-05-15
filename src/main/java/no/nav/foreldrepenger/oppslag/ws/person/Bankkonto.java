package no.nav.foreldrepenger.oppslag.ws.person;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Bankkonto {

    private final String kontonummer;
    private final String banknavn;

    @JsonCreator
    public Bankkonto(@JsonProperty("kontonummer") String kontonummer,
            @JsonProperty("banknavn") String banknavn) {
        this.kontonummer = kontonummer;
        this.banknavn = banknavn;
    }

    public String getKontonummer() {
        return kontonummer;
    }

    public String getBanknavn() {
        return banknavn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        Bankkonto bankkonto = (Bankkonto) o;
        return Objects.equals(kontonummer, bankkonto.kontonummer) &&
                Objects.equals(banknavn, bankkonto.banknavn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kontonummer, banknavn);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [kontonummer=" + kontonummer + ", banknavn=" + banknavn + "]";
    }

}

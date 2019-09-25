package no.nav.foreldrepenger.oppslag.ws.person;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Navn {
    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
    private final Kjønn kjønn;

    @JsonCreator
    public Navn(@JsonProperty("fornavn") String fornavn, @JsonProperty("mellomnavn") String mellomnavn,
            @JsonProperty("etternavn") String etternavn, @JsonProperty("kjønn") Kjønn kjønn) {
        this.fornavn = fornavn;
        this.mellomnavn = mellomnavn;
        this.etternavn = etternavn;
        this.kjønn = kjønn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fornavn, mellomnavn, etternavn, kjønn);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Navn other = (Navn) obj;
        return Objects.equals(this.fornavn, other.fornavn) && Objects.equals(this.mellomnavn, other.mellomnavn)
                && Objects.equals(this.etternavn, other.etternavn) && Objects.equals(this.kjønn, other.kjønn);
    }

    public Kjønn getKjønn() {
        return kjønn;
    }

    public String getFornavn() {
        return fornavn;
    }

    public String getMellomnavn() {
        return mellomnavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    @Override
    public String toString() {
        return "getClass().getSimpleName() [fornavn=" + fornavn + ", mellomnavn=" + mellomnavn + ", etternavn="
                + etternavn + ", kjønn=" + kjønn + "]";
    }
}

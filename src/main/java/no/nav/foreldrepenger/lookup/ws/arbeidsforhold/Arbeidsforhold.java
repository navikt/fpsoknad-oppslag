package no.nav.foreldrepenger.lookup.ws.arbeidsforhold;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Arbeidsforhold extends TidsAvgrensetBrukerInfo {

    private final String arbeidsgiverId;
    private final String arbeidsgiverIdType;
    private final Double stillingsprosent;
    private String arbeidsgiverNavn;

    @JsonCreator
    public Arbeidsforhold(@JsonProperty("arbeidsgiverId") String arbeidsgiverId,
            @JsonProperty("arbeidsgiverIdType") String arbeidsgiverIdType,
            @JsonProperty("stillingsprosent") Double stillingsprosent, @JsonProperty("from") LocalDate from,
            @JsonProperty("to") Optional<LocalDate> to) {
        super(from, to);
        this.arbeidsgiverId = arbeidsgiverId;
        this.arbeidsgiverIdType = arbeidsgiverIdType;
        this.stillingsprosent = stillingsprosent;
    }

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }

    public String getArbeidsgiverIdType() {
        return arbeidsgiverIdType;
    }

    public Double getStillingsprosent() {
        return stillingsprosent;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Arbeidsforhold that = (Arbeidsforhold) o;
        return Objects.equals(arbeidsgiverId, that.arbeidsgiverId)
                && Objects.equals(arbeidsgiverIdType, that.arbeidsgiverIdType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), arbeidsgiverId, arbeidsgiverIdType);
    }

    @Override
    public String toString() {
        return "Arbeidsforhold{" +
                "arbeidsgiverId='" + arbeidsgiverId + '\'' +
                ", arbeidsgiverIdType='" + arbeidsgiverIdType + '\'' +
                '}';
    }
}

package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public abstract class TidsAvgrensetBrukerInfo {

    private final LocalDate fom;
    private final Optional<LocalDate> tom;

    public TidsAvgrensetBrukerInfo(LocalDate fom, Optional<LocalDate> tom) {
        this.fom = fom;
        this.tom = tom;
    }

    public LocalDate getFom() {
        return fom;
    }

    public Optional<LocalDate> getTom() {
        return tom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        TidsAvgrensetBrukerInfo that = (TidsAvgrensetBrukerInfo) o;
        return Objects.equals(fom, that.fom) &&
                Objects.equals(tom, that.tom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fom, tom);
    }

    @Override
    public String toString() {
        return "TidsAvgrensetBrukerInfo{" +
                "fom=" + fom +
                ", tom=" + tom +
                '}';
    }
}

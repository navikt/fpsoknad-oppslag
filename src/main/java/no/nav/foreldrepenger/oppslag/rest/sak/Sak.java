package no.nav.foreldrepenger.oppslag.rest.sak;

import java.time.LocalDate;
import java.util.Objects;

public class Sak {

    private String saksnummer;
    private String sakstype;
    private String fagsystem;
    private String fagsakId;
    private String status;
    private LocalDate opprettet;
    private String opprettetAv;

    public Sak(String saksnummer, String sakstype, String fagsystem, String fagsakId, String status,
            LocalDate opprettet, String opprettetAv) {
        this.saksnummer = saksnummer;
        this.sakstype = sakstype;
        this.fagsystem = fagsystem;
        this.fagsakId = fagsakId;
        this.status = status;
        this.opprettet = opprettet;
        this.opprettetAv = opprettetAv;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getSakstype() {
        return sakstype;
    }

    public String getFagsystem() {
        return fagsystem;
    }

    public String getFagsakId() {
        return fagsakId;
    }

    public LocalDate getOpprettet() {
        return opprettet;
    }

    public String getStatus() {
        return status;
    }

    public String getOpprettetAv() {
        return opprettetAv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Sak sak = (Sak) o;
        return Objects.equals(fagsakId, sak.fagsakId) &&
                Objects.equals(sakstype, sak.sakstype) &&
                Objects.equals(fagsystem, sak.fagsystem) &&
                Objects.equals(saksnummer, sak.saksnummer) &&
                Objects.equals(opprettet, sak.opprettet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fagsakId, sakstype, fagsystem, saksnummer, opprettet);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[saksnummer=" + saksnummer + ", sakstype=" + sakstype + ", fagsystem="
                + fagsystem + ", fagsakId=" + fagsakId + ", status=" + status + ", opprettet=" + opprettet
                + ", opprettetAv=" + opprettetAv + "]";
    }

}

package no.nav.foreldrepenger.oppslag.rest.sak;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoteSak {

    private Long id;
    private String tema;
    private String applikasjon;
    private String aktoerId;
    private String orgnr;
    private String fagsakNr;
    private String opprettetAv;
    private String opprettetTidspunkt;

    public RemoteSak(@JsonProperty("id") Long id,
            @JsonProperty("tema") String tema,
            @JsonProperty("applikasjon") String applikasjon,
            @JsonProperty("aktoerId") String aktoerId,
            @JsonProperty("orgnr") String orgnr,
            @JsonProperty("fagsakNr") String fagsakNr,
            @JsonProperty("opprettetAv") String opprettetAv,
            @JsonProperty("opprettetTidspunkt") String opprettetTidspunkt) {
        this.id = id;
        this.tema = tema;
        this.applikasjon = applikasjon;
        this.aktoerId = aktoerId;
        this.orgnr = orgnr;
        this.fagsakNr = fagsakNr;
        this.opprettetAv = opprettetAv;
        this.opprettetTidspunkt = opprettetTidspunkt;
    }

    public Long getId() {
        return id;
    }

    public String getTema() {
        return tema;
    }

    public String getApplikasjon() {
        return applikasjon;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public String getOrgnr() {
        return orgnr;
    }

    public String getFagsakNr() {
        return fagsakNr;
    }

    public String getOpprettetAv() {
        return opprettetAv;
    }

    public String getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RemoteSak remoteSak = (RemoteSak) o;
        return id.equals(remoteSak.id) &&
                Objects.equals(tema, remoteSak.tema) &&
                Objects.equals(applikasjon, remoteSak.applikasjon) &&
                Objects.equals(aktoerId, remoteSak.aktoerId) &&
                Objects.equals(orgnr, remoteSak.orgnr) &&
                Objects.equals(fagsakNr, remoteSak.fagsakNr) &&
                Objects.equals(opprettetAv, remoteSak.opprettetAv) &&
                Objects.equals(opprettetTidspunkt, remoteSak.opprettetTidspunkt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tema, applikasjon, aktoerId, orgnr, fagsakNr, opprettetAv, opprettetTidspunkt);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + ", tema=" + tema + ", applikasjon=" + applikasjon
                + ", aktoerId=" + aktoerId + ", orgnr=" + orgnr + ", fagsakNr=" + fagsakNr + ", opprettetAv="
                + opprettetAv + ", opprettetTidspunkt=" + opprettetTidspunkt + "]";
    }

}

package no.nav.foreldrepenger.oppslag.stub;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.oppslag.rest.sak.Sak;
import no.nav.foreldrepenger.oppslag.rest.sak.SakClient;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørId;

public class SakClientStub implements SakClient {
    @Override
    @Timed("lookup.sak")
    public List<Sak> sakerFor(AktørId aktor, String tema) {
        return Arrays.asList(
                new Sak("sak1", "typen", "systemet", "fsid1",
                        "status", LocalDate.of(2018, 9, 19), ""),
                new Sak("sak2", "typen", "systemet", "fsid2",
                        "status", LocalDate.of(2018, 9, 18), ""),
                new Sak("sak3", "typen", "systemet", "fsid3",
                        "status", LocalDate.of(2018, 9, 17), ""));
    }
}

package no.nav.foreldrepenger.oppslag.fpsak;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;

import java.util.List;

public interface FpsakClient {

    void ping();

    List<Ytelse> casesFor(AktorId aktor);

}

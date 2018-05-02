package no.nav.foreldrepenger.oppslag.fpsak;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.binding.FinnSakListeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.binding.ForeldrepengesakV1;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.meldinger.FinnSakListeRequest;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.meldinger.FinnSakListeResponse;

public class FpsakClientWs implements FpsakClient {
    private static final Logger log = LoggerFactory.getLogger(FpsakClientWs.class);

    private final ForeldrepengesakV1 fpsakV1;

    private static final Counter ERROR_COUNTER = Metrics.counter("errors.lookup.fpsak");

    @Inject
    public FpsakClientWs(ForeldrepengesakV1 fpsakV1) {
        this.fpsakV1 = Objects.requireNonNull(fpsakV1);

    }

    public void ping() {
        fpsakV1.ping();
    }

    public List<Ytelse> casesFor(AktorId aktor) {
        FinnSakListeRequest req = new FinnSakListeRequest();
        Aktoer a = new Aktoer();
        a.setAktoerId(aktor.getAktør());
        req.setSakspart(a);
        try {
            FinnSakListeResponse res = fpsakV1.finnSakListe(req);
            return res.getSakListe().stream().map(SakMapper::map).collect(toList());
        } catch (FinnSakListeSikkerhetsbegrensning ex) {
            throw new ForbiddenException(ex);
        } catch (Exception ex) {
            log.warn("Error while reading from Fpsak", ex);
            ERROR_COUNTER.increment();
            throw new RuntimeException("Error while reading from Fpsak", ex);
        }
    }

    @Override
    public String toString() {
        return "FpsakKlient{" + "fpsakV1=" + fpsakV1 + '}';
    }
}

package no.nav.foreldrepenger.oppslag.medl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.MedlPeriode;
import no.nav.foreldrepenger.oppslag.domain.exceptions.ForbiddenException;
import no.nav.foreldrepenger.oppslag.domain.exceptions.NotFoundException;
import no.nav.tjeneste.virksomhet.medlemskap.v2.MedlemskapV2;
import no.nav.tjeneste.virksomhet.medlemskap.v2.PersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.medlemskap.v2.Sikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.Foedselsnummer;
import no.nav.tjeneste.virksomhet.medlemskap.v2.informasjon.Personidentifikator;
import no.nav.tjeneste.virksomhet.medlemskap.v2.meldinger.HentPeriodeListeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class MedlClientWs implements MedlClient {
    private static final Logger log = LoggerFactory.getLogger(MedlClientWs.class);

    private final MedlemskapV2 medlemskapV2;

    private final Counter errorCounter = Metrics.counter("errors.lookup.medl");

    @Inject
    public MedlClientWs(MedlemskapV2 medlemskapV2) {
        this.medlemskapV2 = medlemskapV2;
    }

    public void ping() {
        medlemskapV2.ping();
    }

    public List<MedlPeriode> medlInfo(Fodselsnummer fnr) {
        HentPeriodeListeRequest req = new HentPeriodeListeRequest();
        Personidentifikator ident = new Foedselsnummer();
        ident.setValue(fnr.getFnr());
        req.setIdent(ident);
        try {
            return medlemskapV2.hentPeriodeListe(req).getPeriodeListe().stream()
                .map(MedlemsperiodeMapper::map)
                .collect(toList());
        } catch (PersonIkkeFunnet ex) {
            throw new NotFoundException(ex);
        } catch (Sikkerhetsbegrensning ex) {
            log.warn("Sikkerhetsfeil fra MEDL", ex);
            throw new ForbiddenException(ex);
        } catch (Exception ex) {
            errorCounter.increment();
            throw new RuntimeException(ex);
        }
    }

}

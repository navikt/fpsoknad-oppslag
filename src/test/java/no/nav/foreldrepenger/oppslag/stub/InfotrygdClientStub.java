package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.infotrygd.InfotrygdClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class InfotrygdClientStub implements InfotrygdClient {

    private static final Logger LOG = LoggerFactory.getLogger(InfotrygdClient.class);

    @Override
    public void ping() {
        LOG.debug("PONG");

    }

    @Override
    public List<Ytelse> casesFor(Fodselsnummer fnr, LocalDate from, LocalDate to) {
        return null;
    }
}

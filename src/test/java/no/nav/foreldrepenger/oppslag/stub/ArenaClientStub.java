package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.arena.ArenaClient;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class ArenaClientStub implements ArenaClient {

    private static final Logger LOG = LoggerFactory.getLogger(ArenaClientStub.class);


    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public List<Ytelse> ytelser(Fodselsnummer fnr, LocalDate from, LocalDate to) {
        return null;
    }
}

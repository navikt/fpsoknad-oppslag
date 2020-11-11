package no.nav.foreldrepenger.oppslag.stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.oppslag.ws.person.Bankkonto;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.ws.person.PersonTjeneste;

public class PersonClientStub implements PersonTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(PersonClientStub.class);

    @Override
    public void ping() {
        LOG.info("PONG");
    }

    @Override
    public Bankkonto bankkonto(Fødselsnummer fnr) {
        return new Bankkonto("20000000000", "Store Fiskerbank");
    }
}

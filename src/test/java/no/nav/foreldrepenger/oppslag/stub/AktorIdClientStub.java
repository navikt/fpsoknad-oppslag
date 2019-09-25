package no.nav.foreldrepenger.oppslag.stub;

import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktorId;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AktorIdClientStub implements AktorIdClient {

    private static final Logger LOG = LoggerFactory.getLogger(AktorIdClientStub.class);

    @Override
    @Timed("lookup.aktor")
    public AktorId aktorIdForFnr(Fødselsnummer fnr) {
        return new AktorId("Michael learns to rock");
    }

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    @Timed("lookup.fnr")
    public Fødselsnummer fnrForAktørId(AktorId fnr) {
        return new Fødselsnummer("01010100000");
    }

}

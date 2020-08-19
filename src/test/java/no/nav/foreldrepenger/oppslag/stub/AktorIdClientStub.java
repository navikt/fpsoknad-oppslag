package no.nav.foreldrepenger.oppslag.stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørId;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørTjeneste;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;

public class AktorIdClientStub implements AktørTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(AktorIdClientStub.class);

    @Override
    @Timed("lookup.aktor")
    public AktørId aktorIdForFnr(Fødselsnummer fnr) {
        return new AktørId("Michael learns to rock");
    }

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    @Timed("lookup.fnr")
    public Fødselsnummer fnrForAktørId(AktørId fnr) {
        return new Fødselsnummer("01010100000");
    }

}

package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.aareg.AaregClient;
import no.nav.foreldrepenger.oppslag.domain.Arbeidsforhold;
import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AaregClientStub implements AaregClient {

    private static final Logger LOG = LoggerFactory.getLogger(AaregClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public List<Arbeidsforhold> arbeidsforhold(Fodselsnummer fnr) {
        return null;
    }
}

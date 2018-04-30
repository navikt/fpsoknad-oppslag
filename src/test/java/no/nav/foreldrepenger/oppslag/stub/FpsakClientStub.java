package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.domain.AktorId;
import no.nav.foreldrepenger.oppslag.domain.Ytelse;
import no.nav.foreldrepenger.oppslag.fpsak.FpsakClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FpsakClientStub implements FpsakClient {

    private static final Logger LOG = LoggerFactory.getLogger(FpsakClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public List<Ytelse> casesFor(AktorId aktor) {
        return null;
    }
}

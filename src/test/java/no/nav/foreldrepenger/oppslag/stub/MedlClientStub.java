package no.nav.foreldrepenger.oppslag.stub;

import no.nav.foreldrepenger.oppslag.domain.Fodselsnummer;
import no.nav.foreldrepenger.oppslag.domain.MedlPeriode;
import no.nav.foreldrepenger.oppslag.medl.MedlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MedlClientStub implements MedlClient {

    private static final Logger LOG = LoggerFactory.getLogger(MedlClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    public List<MedlPeriode> medlInfo(Fodselsnummer fnr) {
        return null;
    }
}

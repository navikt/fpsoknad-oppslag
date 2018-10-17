package no.nav.foreldrepenger.stub;

import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.OrganisasjonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class OrganisasjonClientStub implements OrganisasjonClient {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisasjonClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    @Timed("lookup.organisasjon")
    public Optional<String> nameFor(String orgnr) {
        return Optional.of("S. Vindel & sønn");
    }

}

package no.nav.foreldrepenger.stub;

import static java.time.LocalDate.now;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.lookup.ws.arbeidsforhold.ArbeidsforholdClient;
import no.nav.foreldrepenger.lookup.ws.person.Fødselsnummer;

public class ArbeidsforholdClientStub implements ArbeidsforholdClient {
    private static final Logger LOG = LoggerFactory.getLogger(ArbeidsforholdClientStub.class);

    @Override
    public void ping() {
        LOG.debug("PONG");
    }

    @Override
    @Timed("lookup.arbeidsforhold")
    public List<Arbeidsforhold> aktiveArbeidsforhold(Fødselsnummer fnr) {
        Arbeidsforhold arbeidsforhold1 = new Arbeidsforhold("0123456789", "orgnummer",
                69d, now().minusYears(1), empty());
        Arbeidsforhold arbeidsforhold2 = new Arbeidsforhold("999999999", "orgnummer",
                100d, now().minusYears(2), of(now().minusYears(1)));
        return Arrays.asList(arbeidsforhold1, arbeidsforhold2);
    }

    @Override
    public String arbeidsgiverNavn(String orgnr) {
        return "Spiders from Mars";
    }
}

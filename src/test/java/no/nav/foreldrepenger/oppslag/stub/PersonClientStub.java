package no.nav.foreldrepenger.oppslag.stub;

import static java.time.LocalDate.now;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.i18n.CountryCode;

import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.oppslag.ws.person.Bankkonto;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.foreldrepenger.oppslag.ws.person.Kjønn;
import no.nav.foreldrepenger.oppslag.ws.person.Navn;
import no.nav.foreldrepenger.oppslag.ws.person.Person;
import no.nav.foreldrepenger.oppslag.ws.person.PersonTjeneste;

public class PersonClientStub implements PersonTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(PersonClientStub.class);

    @Timed("lookup.person")
    @Override
    public Person hentPersonInfo(Fødselsnummer id) {
        Navn navn = new Navn("Anne", "Lene", "Sveen", Kjønn.K);
        return new Person(id, CountryCode.NO, Kjønn.valueOf("M"), navn,
                "NN", new Bankkonto("1234567890", "Stub NOR"),
                now().minusYears(20));
    }

    @Override
    public void ping() {
        LOG.info("PONG");
    }

}

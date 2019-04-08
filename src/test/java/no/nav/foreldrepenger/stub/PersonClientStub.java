package no.nav.foreldrepenger.stub;

import com.neovisionaries.i18n.CountryCode;
import io.micrometer.core.annotation.Timed;
import no.nav.foreldrepenger.lookup.ws.person.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Collections.singletonList;

public class PersonClientStub implements PersonClient {

    private static final Logger LOG = LoggerFactory.getLogger(PersonClientStub.class);

    @Timed("lookup.person")
    @Override
    public Person hentPersonInfo(ID id) {
        Navn navn = new Navn("Skjegg", "Stub", "Sveen");
        return new Person(id, CountryCode.NO, Kjønn.valueOf("M"), navn,
                "NN", new Bankkonto("1234567890", "Stub NOR"),
                now().minusYears(20), barn(id.getFnr()));
    }

    @Override
    public void ping() {
        LOG.info("PONG");

    }

    private List<Barn> barn(Fødselsnummer fnrMor) {
        Barn barn = new Barn(fnrMor,
                new Fødselsnummer("01011812345"),
                now().minusYears(1),
                new Navn("Mo", null, "Sveen"),
                Kjønn.M,
                new AnnenForelder(new Navn("Anne", "N", "Forelder"), new Fødselsnummer("01019012345"),
                        now().minusYears(25)));

        return singletonList(barn);
    }

    @Override
    public Navn navn(Fødselsnummer fnr) {
      return new Navn("Ole", "Gunnar", "Solskjær")
    }
}

package no.nav.foreldrepenger.oppslag.stub;

import com.neovisionaries.i18n.CountryCode;
import no.nav.foreldrepenger.oppslag.domain.*;
import no.nav.foreldrepenger.oppslag.person.PersonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class PersonClientStub implements PersonClient {

    private static final Logger LOG = LoggerFactory.getLogger(PersonClientStub.class);

    @Override
    public Person hentPersonInfo(ID id) {
        Navn navn = new Navn("Jan", "H.", "Johansen");
        return new Person(id, CountryCode.NO, Kjonn.valueOf("M"), navn,
            "nynoregsk", new Bankkonto("1234567890", "Pæng r'us"),
            LocalDate.now().minusYears(20));
    }

    @Override
    public void ping() {
        LOG.info("PONG");

    }
}

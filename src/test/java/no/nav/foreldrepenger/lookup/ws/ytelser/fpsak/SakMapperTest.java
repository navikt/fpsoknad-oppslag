package no.nav.foreldrepenger.lookup.ws.ytelser.fpsak;

import no.nav.foreldrepenger.lookup.ws.ytelser.Ytelse;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.informasjon.Sak;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SakMapperTest {

    @Test
    public void mapValues() throws Exception {
        Sak sak = TestdataProvider.sak();
        Ytelse expected = new Ytelse("temaet",
                "statusen",
                LocalDate.of(2017, 12, 13),
                Optional.empty());
        Ytelse actual = SakMapper.map(sak);
        assertEquals(expected, actual);
    }

}

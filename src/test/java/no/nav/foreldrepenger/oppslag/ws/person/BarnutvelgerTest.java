package no.nav.foreldrepenger.oppslag.ws.person;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class BarnutvelgerTest {
    private static final Fødselsnummer MOR = new Fødselsnummer("28016432662");
    private static final String BARN_FNR = DateTimeFormatter.ofPattern("ddMMyy").format(now().minusMonths(2)) + "36325";

    @Test
    public void testStønadsberettigetRelasjon() {
        int months = 2;
        Barn barn = new Barn(MOR, new Fødselsnummer(BARN_FNR), now().minusMonths(1), navn(), null);
        assertThat(new BarnMorRelasjonSjekkendeBarnutvelger(months).erStonadsberettigetBarn(MOR, barn)).isTrue();
        barn = new Barn(MOR, new Fødselsnummer(BARN_FNR), now().minusMonths(3), navn(), null);
        assertThat(new BarnMorRelasjonSjekkendeBarnutvelger(months).erStonadsberettigetBarn(MOR, barn)).isFalse();
    }

    private static Navn navn() {
        return new Navn("Test", "T", "Testesen", Kjønn.M);
    }
}

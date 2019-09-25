package no.nav.foreldrepenger.oppslag.ws.person;

import static java.time.LocalDate.now;

public class BarnMorRelasjonSjekkendeBarnutvelger implements Barnutvelger {

    private final int monthsBack;

    public BarnMorRelasjonSjekkendeBarnutvelger(int months) {
        this.monthsBack = months;
    }

    @Override
    public boolean erStonadsberettigetBarn(Fødselsnummer fnrSøker, Barn barn) {
        return fnrSøker.equals(barn.getFnrSøker()) && barn.getFødselsdato().isAfter(now().minusMonths(monthsBack));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [monthsBack=" + monthsBack + "]";
    }
}

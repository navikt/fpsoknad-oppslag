package no.nav.foreldrepenger.oppslag.ws.person;

import static java.time.LocalDate.now;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BarnMorRelasjonSjekkendeBarnutvelger implements Barnutvelger {
    private static final Logger LOG = LoggerFactory.getLogger(BarnMorRelasjonSjekkendeBarnutvelger.class);

    private final int monthsBack;

    public BarnMorRelasjonSjekkendeBarnutvelger(int months) {
        this.monthsBack = months;
    }

    @Override
    public boolean erStonadsberettigetBarn(Fødselsnummer fnrSøker, Barn barn) {
        var berettiget = fnrSøker.equals(barn.getFnrSøker()) && barn.getFødselsdato().isAfter(now().minusMonths(monthsBack));
        LOG.info("Barnet er {} berettiget", berettiget ? "KLART" : "IKKE");
        return berettiget;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [monthsBack=" + monthsBack + "]";
    }
}

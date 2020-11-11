package no.nav.foreldrepenger.oppslag.ws.person;

import java.util.Optional;

import no.nav.foreldrepenger.oppslag.util.Pair;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoNorge;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;

final class PersonMapper {

    private PersonMapper() {
    }

    public static Bankkonto kontonr(Person person) {
        return bankkonto(person);
    }

    private static Bankkonto bankkonto(Person person) {
        if (person instanceof Bruker bruker) {
            var bankkonto = bruker.getBankkonto();
            return Optional.ofNullable(bankkonto)
                    .map(PersonMapper::kontoinfo)
                    .map(i -> new Bankkonto(i.getFirst(), i.getSecond()))
                    .orElse(null);
        }
        return null;
    }

    private static Pair<String, String> kontoinfo(no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto konto) {
        if (konto instanceof BankkontoNorge norskKonto) {
            return Pair.of(norskKonto.getBankkonto().getBankkontonummer(), norskKonto.getBankkonto().getBanknavn());
        }
        BankkontoUtland utenlandskKonto = (BankkontoUtland) konto;
        return Pair.of(utenlandskKonto.getBankkontoUtland().getSwift(),
                utenlandskKonto.getBankkontoUtland().getBankkode());
    }
}

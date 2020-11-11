package no.nav.foreldrepenger.oppslag.ws.person;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.oppslag.util.DateUtil;
import no.nav.foreldrepenger.oppslag.util.Pair;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoNorge;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personnavn;

final class PersonMapper {

    private PersonMapper() {
    }

    public static no.nav.foreldrepenger.oppslag.ws.person.Person person(Fødselsnummer fnr,
            no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person, List<Barn> barn) {
        return new no.nav.foreldrepenger.oppslag.ws.person.Person(
                fnr,
                countryCode(person),
                Kjønn.valueOf(person.getKjoenn().getKjoenn().getValue()),
                name(person.getPersonnavn(), Kjønn.valueOf(person.getKjoenn().getKjoenn().getValue())),
                målform(person),
                bankkonto(person),
                birthDate(person),
                barn);
    }

    public static Barn barn(NorskIdent id, Fødselsnummer fnrMor,
            no.nav.tjeneste.virksomhet.person.v3.informasjon.Person barn, AnnenForelder annenForelder) {
        return new Barn(
                fnrMor,
                new Fødselsnummer(id.getIdent()),
                birthDate(barn),
                name(barn.getPersonnavn(), Kjønn.valueOf(barn.getKjoenn().getKjoenn().getValue())),
                annenForelder);
    }

    public static AnnenForelder annenForelder(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person annenForelder) {
        return new AnnenForelder(
                name(annenForelder.getPersonnavn(), Kjønn.valueOf(annenForelder.getKjoenn().getKjoenn().getValue())),
                new Fødselsnummer(PersonIdent.class.cast(annenForelder.getAktoer()).getIdent().getIdent()),
                birthDate(annenForelder));
    }

    static Navn name(Personnavn navn, Kjønn kjønn) {
        return new Navn(navn.getFornavn(), navn.getMellomnavn(), navn.getEtternavn(), kjønn);
    }

    private static CountryCode countryCode(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        if (person.getStatsborgerskap() != null) {
            return countryCode(person.getStatsborgerskap().getLand().getValue());
        }
        return CountryCode.NO;
    }

    private static CountryCode countryCode(String land) {
        return Optional.ofNullable(land)
                .map(CountryCode::getByCode)
                .filter(Objects::nonNull)
                .orElse(CountryCode.NO);
    }

    private static LocalDate birthDate(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        return Optional.ofNullable(person)
                .map(p -> p.getFoedselsdato())
                .map(d -> d.getFoedselsdato())
                .map(DateUtil::toLocalDate)
                .orElse(null);
    }

    private static String målform(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
        if (person instanceof Bruker bruker) {
            return Optional.ofNullable(bruker.getMaalform())
                    .map(m -> m.getValue())
                    .orElse(null);
        }
        return null;
    }

    private static Bankkonto bankkonto(no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person) {
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

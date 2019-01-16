package no.nav.foreldrepenger.lookup.ws.person;

import no.nav.foreldrepenger.lookup.util.DateUtil;
import no.nav.foreldrepenger.lookup.ws.aktor.AktorId;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.lookup.ws.person.PersonMapper.person;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonMapperTest {

    @Test
    public void basePerson() {
        ID id = new ID(new AktorId("123445"), new Fødselsnummer("123456378910"));
        no.nav.foreldrepenger.lookup.ws.person.Person mapped = person(id, createBasePerson(), emptyList());
        assertEquals("123456378910", mapped.getId().getFnr().getFnr());
        assertEquals("Diego", mapped.getNavn().getFornavn());
        assertEquals("Armando", mapped.getNavn().getMellomnavn());
        assertEquals("Maradona", mapped.getNavn().getEtternavn());
    }

    @Test
    public void withMålform() {
        ID id = new ID(new AktorId("123445"), new Fødselsnummer("123456378910"));
        Bruker tpsPerson = personWithMålform();
        no.nav.foreldrepenger.lookup.ws.person.Person mapped = person(id, tpsPerson, emptyList());
        assertEquals("Turkmenistansk", mapped.getMålform());
    }

    @Test
    public void withNorskKonto() {
        ID id = new ID(new AktorId("123445"), new Fødselsnummer("123456378910"));
        Bruker tpsPerson = personWithNorskKonto();
        no.nav.foreldrepenger.lookup.ws.person.Person mapped = person(id, tpsPerson, emptyList());
        Bankkonto expected = new Bankkonto("1234567890", "Ripoff inc.");
        assertEquals(expected, mapped.getBankkonto());
    }

    @Test
    public void withUtenlandskKonto() {
        ID id = new ID(new AktorId("123445"), new Fødselsnummer("123456378910"));
        Bruker tpsPerson = personWithUtenlandskKonto();
        Person mapped = person(id, tpsPerson, emptyList());
        Bankkonto expected = new Bankkonto("swiftster", "bankkode");
        assertEquals(expected, mapped.getBankkonto());
    }

    private Bruker createBasePerson() {
        Bruker person = new Bruker();

        Kjoenn kjoenn = new Kjoenn();
        Kjoennstyper kjoennstyper = new Kjoennstyper();
        kjoennstyper.setValue("K");
        kjoenn.setKjoenn(kjoennstyper);
        person.setKjoenn(kjoenn);

        Personnavn navn = new Personnavn();
        navn.setFornavn("Diego");
        navn.setMellomnavn("Armando");
        navn.setEtternavn("Maradona");
        person.setPersonnavn(navn);

        Gateadresse gateAdresse = new Gateadresse();
        gateAdresse.setGatenavn("Veien");
        gateAdresse.setHusnummer(42);
        gateAdresse.setTilleggsadresseType("OFFISIELL ADRESSE");
        Postnummer postnummer = new Postnummer();
        postnummer.setValue("0175");
        gateAdresse.setPoststed(postnummer);
        Landkoder landkoder = new Landkoder();
        landkoder.setValue("NO");
        gateAdresse.setLandkode(landkoder);
        Bostedsadresse bostedsadresse = new Bostedsadresse();
        bostedsadresse.setStrukturertAdresse(gateAdresse);
        person.setBostedsadresse(bostedsadresse);

        Foedselsdato foedselsdato = new Foedselsdato();
        foedselsdato.setFoedselsdato(DateUtil.toXMLGregorianCalendar(LocalDate.now()));
        person.setFoedselsdato(foedselsdato);

        return person;
    }

    private Bruker personWithMålform() {
        Bruker basePerson = createBasePerson();
        Spraak spraak = new Spraak();
        spraak.setValue("Turkmenistansk");
        basePerson.setMaalform(spraak);
        return basePerson;
    }

    private Bruker personWithNorskKonto() {
        Bruker basePerson = createBasePerson();
        basePerson.setBankkonto(norskKonto());
        return basePerson;
    }

    private Bruker personWithUtenlandskKonto() {
        Bruker basePerson = createBasePerson();
        basePerson.setBankkonto(utenlandskKonto());
        return basePerson;
    }

    private no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto norskKonto() {
        BankkontoNorge bankkonto = new BankkontoNorge();
        Bankkontonummer kontonr = new Bankkontonummer();
        kontonr.setBankkontonummer("1234567890");
        kontonr.setBanknavn("Ripoff inc.");
        bankkonto.setBankkonto(kontonr);
        return bankkonto;
    }

    private no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto utenlandskKonto() {
        BankkontoUtland bankkonto = new BankkontoUtland();
        BankkontonummerUtland kontonr = new BankkontonummerUtland();
        kontonr.setSwift("swiftster");
        kontonr.setBankkode("bankkode");
        bankkonto.setBankkontoUtland(kontonr);
        return bankkonto;
    }

}

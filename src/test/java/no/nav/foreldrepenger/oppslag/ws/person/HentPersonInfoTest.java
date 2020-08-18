package no.nav.foreldrepenger.oppslag.ws.person;

import static java.time.LocalDate.now;
import static no.nav.foreldrepenger.oppslag.ws.WSTestUtil.soapFault;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.cxf.ws.security.trust.STSClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import no.nav.foreldrepenger.oppslag.config.EndpointSTSClientConfig;
import no.nav.foreldrepenger.oppslag.error.NotFoundException;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.OnBehalfOfOutInterceptor;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktørId;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.feil.PersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjoner;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Foedselsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kjoenn;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kjoennstyper;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personidenter;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personnavn;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@EnableRetry
@ContextConfiguration(classes = {
        OnBehalfOfOutInterceptor.class,
        EndpointSTSClientConfig.class,
        PersonConfiguration.class })

public class HentPersonInfoTest {

    @Autowired
    private PersonTjeneste klient;

    @Mock
    private Barnutvelger barnutvelger;

    @MockBean
    @Qualifier(PersonConfiguration.PERSON_V3)
    private PersonV3 tps;

    @MockBean
    private TokenUtil tokenHandler;

    @MockBean
    STSClient sts;

    @Test
    public void testHentingAvPersonMedBarnSkalKallePåTpsToGanger() throws Exception {
        when(tps.hentPerson(any())).thenReturn(response(barn("11111898765", "FNR")));
        klient.hentPersonInfo(id());
        verify(tps, times(2)).hentPerson(any());
    }

    @Test
    public void testPersonIkkeFunnetIngenRetry() throws Exception {
        when(tps.hentPerson(any()))
                .thenThrow(new HentPersonPersonIkkeFunnet("Fant ikke", new PersonIkkeFunnet()));
        assertThrows(NotFoundException.class, () -> {
            klient.hentPersonInfo(id());
        });
        verify(tps).hentPerson(any());
    }

    @Test
    public void testRetryUntilFail() throws Exception {
        when(tps.hentPerson(any()))
                .thenThrow(soapFault());
        assertThrows(SOAPFaultException.class, () -> klient.hentPersonInfo(id()));
        verify(tps, times(3)).hentPerson(any());
    }

    @Test
    public void testFailThenOK() throws Exception {
        when(tps.hentPerson(any()))
                .thenThrow(soapFault())
                .thenReturn(response(barn("11111898765", "FNR")));
        klient.hentPersonInfo(id());
        verify(tps, times(3)).hentPerson(any());
    }

    @Test
    public void testHentingAvPersonMedFdatBarnSkalKallePåTpsEnGang() throws Exception {
        when(tps.hentPerson(any())).thenReturn(response(barn("11111800000", "FDAT")));

        klient.hentPersonInfo(id());
        verify(tps).hentPerson(any());
    }

    private static ID id() {
        return new ID(new AktørId("aktør"), new Fødselsnummer("fnr"));
    }

    private static HentPersonResponse response(Familierelasjon rel) {
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person person = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Person();
        person.setAktoer(aktoer("01018812345", "FNR"));
        person.setKjoenn(kjoenn());
        person.setPersonnavn(navn());
        person.setFoedselsdato(foedselsdato());

        if (rel != null) {
            person.getHarFraRolleI().add(rel);
        }

        HentPersonResponse response = new HentPersonResponse();
        response.setPerson(person);

        return response;
    }

    private static Familierelasjon barn(String fnr, String fnrType) {
        Familierelasjon rel = new Familierelasjon();
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Person barn = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Person();
        barn.setAktoer(aktoer(fnr, fnrType));
        rel.setTilPerson(barn);
        Familierelasjoner type = new Familierelasjoner();
        type.setValue("BARN");
        rel.setTilRolle(type);
        return rel;
    }

    private static Foedselsdato foedselsdato() {
        Foedselsdato foedselsdato = new Foedselsdato();
        LocalDate twenty = now().minusYears(20);
        GregorianCalendar gcal = GregorianCalendar.from(twenty.atStartOfDay(ZoneId.systemDefault()));
        XMLGregorianCalendar xcal = null;
        try {
            xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (DatatypeConfigurationException ignored) {
        }
        foedselsdato.setFoedselsdato(xcal);
        return foedselsdato;
    }

    private static Personnavn navn() {
        Personnavn navn = new Personnavn();
        navn.setFornavn("Test");
        navn.setEtternavn("Testesen");
        return navn;
    }

    private static Kjoenn kjoenn() {
        Kjoenn kjoenn = new Kjoenn();
        Kjoennstyper type = new Kjoennstyper();
        type.setValue("K");
        kjoenn.setKjoenn(type);
        return kjoenn;
    }

    private static Aktoer aktoer(String fnr, String fnrType) {
        PersonIdent aktoer = new PersonIdent();
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(fnr);
        Personidenter type = new Personidenter();
        type.setValue(fnrType);
        norskIdent.setType(type);
        aktoer.setIdent(norskIdent);

        return aktoer;
    }

}

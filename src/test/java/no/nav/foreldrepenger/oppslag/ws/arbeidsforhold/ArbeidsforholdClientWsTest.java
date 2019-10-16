package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold;

import static no.nav.foreldrepenger.oppslag.ws.WSTestUtil.retriedOK;
import static no.nav.foreldrepenger.oppslag.ws.WSTestUtil.soapFault;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.oppslag.error.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.error.UnauthorizedException;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.ArbeidsforholdV3;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.binding.FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.AnsettelsesPeriode;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Gyldighetsperiode;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Organisasjon;
import no.nav.tjeneste.virksomhet.arbeidsforhold.v3.meldinger.FinnArbeidsforholdPrArbeidstakerResponse;
import no.nav.tjeneste.virksomhet.organisasjon.v5.binding.HentOrganisasjonUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjon.v5.binding.OrganisasjonV5;
import no.nav.tjeneste.virksomhet.organisasjon.v5.informasjon.SammensattNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v5.informasjon.UstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v5.meldinger.HentOrganisasjonResponse;

@ExtendWith(MockitoExtension.class)
public class ArbeidsforholdClientWsTest {

    private static final String LURIUM_AS = "Lurium AS";
    private static final String ORGNR = "999999999";
    private static final String YRKE = "snekker";
    private static final String NAVN = "S. Vindel & sønn";
    private static final LocalDate FOURYEARSAGO = LocalDate.now().minusYears(4);
    private static final LocalDate TWOYEARSAGO = LocalDate.now().minusYears(2);

    private static final LocalDate LASTWEEK = LocalDate.now().minusDays(7);
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(2);
    private static final Fødselsnummer FNR = new Fødselsnummer("22222222222");
    @Mock
    private ArbeidsforholdV3 arbeidsforhold;
    @Mock
    private ArbeidsforholdV3 healthIndicator;
    private OrganisasjonClient orgClient;
    @Mock
    private TokenUtil tokenHandler;
    @Mock
    private OrganisasjonV5 organisasjonV5;
    @Mock
    private OrganisasjonV5 orgHealth;

    private ArbeidsforholdClientWs client;

    @BeforeEach
    public void beforeEach() {
        orgClient = new OrganisasjonClientWs(organisasjonV5, orgHealth, tokenHandler);
        client = new ArbeidsforholdClientWs(arbeidsforhold, healthIndicator, orgClient,
                tokenHandler);
    }

    @Test
    public void testRetryArbeidsforholdUntilFail() throws Exception {
        when(arbeidsforhold.finnArbeidsforholdPrArbeidstaker(any()))
                .thenThrow(soapFault());
        assertThrows(SOAPFaultException.class, () -> {
            client.aktiveArbeidsforhold(FNR);
        });
        verify(arbeidsforhold, retriedOK()).finnArbeidsforholdPrArbeidstaker(any());
    }

    @Test
    public void testarbeidsforholdExpiredTokenDoesNotRetry() throws Exception {
        when(tokenHandler.isExpired()).thenReturn(true);
        when(arbeidsforhold.finnArbeidsforholdPrArbeidstaker(any()))
                .thenThrow(soapFault());
        assertThrows(TokenExpiredException.class, () -> {
            client.aktiveArbeidsforhold(FNR);
        });
        verify(arbeidsforhold).finnArbeidsforholdPrArbeidstaker(any());
    }

    @Test
    public void testArbeidsforholdNonSoapExceptionDoesNotRetry() throws Exception {
        when(arbeidsforhold.finnArbeidsforholdPrArbeidstaker(any()))
                .thenThrow(new FinnArbeidsforholdPrArbeidstakerSikkerhetsbegrensning(null, null));
        assertThrows(UnauthorizedException.class, () -> {
            client.aktiveArbeidsforhold(FNR);
        });
        verify(arbeidsforhold).finnArbeidsforholdPrArbeidstaker(any());
    }

    @Test
    public void testRetryRecoversBoth() throws Exception {
        when(organisasjonV5.hentOrganisasjon(any()))
                .thenThrow(soapFault())
                .thenReturn(hentOrgResponse());
        when(arbeidsforhold.finnArbeidsforholdPrArbeidstaker(any()))
                .thenThrow(soapFault())
                .thenReturn(respons());
        List<Arbeidsforhold> aktiveArbeidsforhold = client.aktiveArbeidsforhold(FNR);
        assertEquals(aktiveArbeidsforhold.size(), 1);
        assertEquals(aktiveArbeidsforhold.get(0).getArbeidsgiverNavn(), LURIUM_AS);
        verify(arbeidsforhold, retriedOK(2)).finnArbeidsforholdPrArbeidstaker(any());
        verify(organisasjonV5, retriedOK(2)).hentOrganisasjon(any());
    }

    @Test
    public void testBothOK() throws Exception {
        when(organisasjonV5.hentOrganisasjon(any()))
                .thenReturn(hentOrgResponse());
        when(arbeidsforhold.finnArbeidsforholdPrArbeidstaker(any()))
                .thenReturn(respons());
        List<Arbeidsforhold> aktiveArbeidsforhold = client.aktiveArbeidsforhold(FNR);
        assertEquals(aktiveArbeidsforhold.size(), 1);
        assertEquals(aktiveArbeidsforhold.get(0).getArbeidsgiverNavn(), LURIUM_AS);
        verify(arbeidsforhold).finnArbeidsforholdPrArbeidstaker(any());
        verify(organisasjonV5).hentOrganisasjon(any());
    }

    @Test
    public void OrgFailInvalidExceptionGivesNoName() throws Exception {
        when(organisasjonV5.hentOrganisasjon(any()))
                .thenThrow(new HentOrganisasjonUgyldigInput(null, null));
        when(arbeidsforhold.finnArbeidsforholdPrArbeidstaker(any()))
                .thenReturn(respons());
        List<Arbeidsforhold> aktiveArbeidsforhold = client.aktiveArbeidsforhold(FNR);
        assertEquals(aktiveArbeidsforhold.size(), 1);
        verify(arbeidsforhold).finnArbeidsforholdPrArbeidstaker(any());
        verify(organisasjonV5).hentOrganisasjon(any());
    }

    @Test
    public void OrgFailSOAPExceptionGivesNoName() throws Exception {
        when(organisasjonV5.hentOrganisasjon(any()))
                .thenThrow(soapFault());
        when(arbeidsforhold.finnArbeidsforholdPrArbeidstaker(any()))
                .thenReturn(respons());
        List<Arbeidsforhold> aktiveArbeidsforhold = client.aktiveArbeidsforhold(FNR);
        assertEquals(aktiveArbeidsforhold.size(), 1);
        verify(arbeidsforhold).finnArbeidsforholdPrArbeidstaker(any());
        verify(organisasjonV5, retriedOK()).hentOrganisasjon(any());
    }

    @Test
    public void testOrganisasjonRetryUntilFail() throws Exception {
        when(organisasjonV5.hentOrganisasjon(any()))
                .thenThrow(soapFault());
        when(arbeidsforhold.finnArbeidsforholdPrArbeidstaker(any()))
                .thenReturn(respons());
        client.aktiveArbeidsforhold(FNR);
        verify(arbeidsforhold).finnArbeidsforholdPrArbeidstaker(any());
        verify(organisasjonV5, retriedOK()).hentOrganisasjon(any());
    }

    @Test
    public void noEndDateSet() {
        assertTrue(client.siste3år(new Arbeidsforhold(NAVN, YRKE, 100.0, LASTWEEK, Optional.empty())));
    }

    @Test
    public void endDate4årSiden() {
        assertFalse(
                client.siste3år(new Arbeidsforhold(NAVN, YRKE, 100.0, LASTWEEK, Optional.ofNullable(FOURYEARSAGO))));
    }

    @Test
    public void endDate2årSiden() {
        assertTrue(
                client.siste3år(new Arbeidsforhold(NAVN, YRKE, 100.0, LASTWEEK, Optional.ofNullable(TWOYEARSAGO))));
    }

    @Test
    public void endDateInTheFuture() {
        assertTrue(client.siste3år(new Arbeidsforhold(NAVN, YRKE, 100.0, LASTWEEK, Optional.ofNullable(TOMORROW))));
    }

    private FinnArbeidsforholdPrArbeidstakerResponse respons() throws Exception {
        FinnArbeidsforholdPrArbeidstakerResponse respons = new FinnArbeidsforholdPrArbeidstakerResponse();
        respons.getArbeidsforhold().add(af());
        return respons;
    }

    private no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold af()
            throws Exception {
        no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold af = new no.nav.tjeneste.virksomhet.arbeidsforhold.v3.informasjon.arbeidsforhold.Arbeidsforhold();
        Organisasjon aktør = new Organisasjon();
        aktør.setOrgnummer(ORGNR);
        af.setArbeidsgiver(aktør);
        AnsettelsesPeriode periode = new AnsettelsesPeriode();
        Gyldighetsperiode gyldighet = new Gyldighetsperiode();
        GregorianCalendar now = new GregorianCalendar();
        XMLGregorianCalendar fom = DatatypeFactory.newInstance().newXMLGregorianCalendar(now);
        gyldighet.setFom(fom);
        periode.setPeriode(gyldighet);
        af.setAnsettelsesPeriode(periode);
        return af;
    }

    private HentOrganisasjonResponse hentOrgResponse() {
        HentOrganisasjonResponse respons = new HentOrganisasjonResponse();
        respons.setOrganisasjon(org());
        return respons;
    }

    private no.nav.tjeneste.virksomhet.organisasjon.v5.informasjon.Organisasjon org() {
        no.nav.tjeneste.virksomhet.organisasjon.v5.informasjon.Organisasjon org = new no.nav.tjeneste.virksomhet.organisasjon.v5.informasjon.Organisasjon();
        org.setOrgnummer(ORGNR);
        org.setNavn(navn());
        return org;
    }

    private SammensattNavn navn() {
        UstrukturertNavn navn = new UstrukturertNavn();
        navn.getNavnelinje().add(LURIUM_AS);
        return navn;
    }
}

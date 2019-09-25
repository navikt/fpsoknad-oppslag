package no.nav.foreldrepenger.oppslag.ws.aktor;

import static no.nav.foreldrepenger.oppslag.ws.WSTestUtil.retriedOK;
import static no.nav.foreldrepenger.oppslag.ws.WSTestUtil.soapFault;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.oppslag.errorhandling.NotFoundException;
import no.nav.foreldrepenger.oppslag.errorhandling.TokenExpiredException;
import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktorId;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktorIdClient;
import no.nav.foreldrepenger.oppslag.ws.aktor.AktorIdClientWs;
import no.nav.foreldrepenger.oppslag.ws.person.Fødselsnummer;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.HentIdentForAktoerIdPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdResponse;

@ExtendWith(MockitoExtension.class)

public class AktørWSClientTest {

    private static final AktorId AKTOR = new AktorId("222222222");
    private static final Fødselsnummer FNR = new Fødselsnummer("22222222222");
    @Mock
    private TokenUtil tokenHandler;
    @Mock
    private AktoerV2 healthIndicator;
    @Mock
    private AktoerV2 aktoerV2;
    private AktorIdClient aktørClient;

    @BeforeEach
    public void beforeEach() {
        aktørClient = new AktorIdClientWs(aktoerV2, healthIndicator, tokenHandler);
    }

    @Test
    public void restRetryUntilFailAktør() throws Exception {
        when(aktoerV2.hentAktoerIdForIdent(any()))
                .thenThrow(soapFault());
        assertThrows(SOAPFaultException.class, () -> {
            aktørClient.aktorIdForFnr(FNR);
        });
        verify(aktoerV2, retriedOK()).hentAktoerIdForIdent(any());
    }

    @Test
    public void restRetryAktørRecovery() throws Exception {
        when(aktoerV2.hentAktoerIdForIdent(any()))
                .thenThrow(soapFault())
                .thenReturn(aktørResponse());
        aktørClient.aktorIdForFnr(FNR);
        verify(aktoerV2, retriedOK()).hentAktoerIdForIdent(any());
    }

    @Test
    public void otherExceptionAktørNoRetry() throws Exception {
        when(aktoerV2.hentAktoerIdForIdent(any()))
                .thenThrow(new HentAktoerIdForIdentPersonIkkeFunnet(null, null));
        assertThrows(NotFoundException.class, () -> aktørClient.aktorIdForFnr(FNR));
        verify(aktoerV2).hentAktoerIdForIdent(any());
    }

    @Test
    public void soapFaultAktørAndExpiredNoRetry() throws Exception {
        when(tokenHandler.isExpired()).thenReturn(true);
        when(aktoerV2.hentAktoerIdForIdent(any()))
                .thenThrow(soapFault());
        assertThrows(TokenExpiredException.class, () -> aktørClient.aktorIdForFnr(FNR));
        verify(aktoerV2).hentAktoerIdForIdent(any());
    }

    @Test
    public void restRetryUntilFailFnr() throws Exception {
        when(aktoerV2.hentIdentForAktoerId(any()))
                .thenThrow(soapFault());
        assertThrows(SOAPFaultException.class, () -> {
            aktørClient.fnrForAktørId(AKTOR);
        });
        verify(aktoerV2, retriedOK()).hentIdentForAktoerId(any());
    }

    @Test
    public void restRetryFnrRecovery() throws Exception {
        when(aktoerV2.hentIdentForAktoerId(any()))
                .thenThrow(soapFault())
                .thenReturn(fnrResponse());
        aktørClient.fnrForAktørId(AKTOR);
        verify(aktoerV2, retriedOK()).hentIdentForAktoerId(any());
    }

    @Test
    public void otherExceptionFnrNoRetry() throws Exception {
        when(aktoerV2.hentIdentForAktoerId(any()))
                .thenThrow(new HentIdentForAktoerIdPersonIkkeFunnet(null, null));
        assertThrows(NotFoundException.class, () -> aktørClient.fnrForAktørId(AKTOR));
        verify(aktoerV2).hentIdentForAktoerId(any());
    }

    @Test
    public void soapFaultFnrAndExpiredNoRetry() throws Exception {
        when(tokenHandler.isExpired()).thenReturn(true);
        when(aktoerV2.hentIdentForAktoerId(any()))
                .thenThrow(soapFault());
        assertThrows(TokenExpiredException.class, () -> aktørClient.fnrForAktørId(AKTOR));
        verify(aktoerV2).hentIdentForAktoerId(any());
    }

    private HentAktoerIdForIdentResponse aktørResponse() {
        HentAktoerIdForIdentResponse respons = new HentAktoerIdForIdentResponse();
        respons.setAktoerId(AKTOR.getAktør());
        return respons;
    }

    private static HentIdentForAktoerIdResponse fnrResponse() {
        HentIdentForAktoerIdResponse respons = new HentIdentForAktoerIdResponse();
        respons.setIdent(FNR.getFnr());
        return respons;
    }
}

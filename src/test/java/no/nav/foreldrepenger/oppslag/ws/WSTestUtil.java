package no.nav.foreldrepenger.oppslag.ws;

import static no.nav.foreldrepenger.oppslag.util.RetryUtil.DEFAULT_RETRIES;
import static org.mockito.Mockito.times;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.soap.SOAPFaultException;

import org.mockito.verification.VerificationMode;

public class WSTestUtil {
    public static SOAPFaultException soapFault() throws SOAPException {
        return new SOAPFaultException(SOAPFactory.newInstance().createFault("Dette gikk skikkelig dålig, kompis",
                new QName("http://schemas.xmlsoap.org/soap/envelope/", "Client")));
    }

    public static VerificationMode retriedOK() {
        return times(DEFAULT_RETRIES);
    }

    public static VerificationMode retriedOK(int retries) {
        return times(retries);
    }
}

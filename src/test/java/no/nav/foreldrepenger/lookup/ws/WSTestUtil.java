package no.nav.foreldrepenger.lookup.ws;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.soap.SOAPFaultException;

public class WSTestUtil {
    public static SOAPFaultException soapFault() throws SOAPException {
        return new SOAPFaultException(SOAPFactory.newInstance().createFault("Dette gikk skikkelig dålig, kompis",
                new QName("http://schemas.xmlsoap.org/soap/envelope/", "Client")));
    }
}

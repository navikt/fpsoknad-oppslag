package no.nav.foreldrepenger.lookup.ws;

import static javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.CONFIDENTIAL;
import static org.apache.cxf.rt.security.SecurityConstants.STS_TOKEN_ON_BEHALF_OF;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.lookup.util.TokenUtil;

@Component
@Scope(SCOPE_PROTOTYPE)
public class OnBehalfOfOutInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(OnBehalfOfOutInterceptor.class);

    private static final String OIDC_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:jwt";
    private final TokenUtil tokenHandler;

    public OnBehalfOfOutInterceptor(TokenUtil tokenHandler) {
        super(Phase.SETUP);
        this.tokenHandler = tokenHandler;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        LOG.debug("Slår opp OnBehalfOfToken");
        String token = tokenHandler.getToken();
        LOG.debug(CONFIDENTIAL, "Fant token {}", token);
        message.put(STS_TOKEN_ON_BEHALF_OF, createOnBehalfOfElement(token));
    }

    private static Element createOnBehalfOfElement(String token) {
        try {
            String content = wrapWithBinarySecurityToken(token.getBytes());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(FEATURE_SECURE_PROCESSING, true);
            return factory.newDocumentBuilder().parse(new InputSource(new StringReader(content))).getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String wrapWithBinarySecurityToken(byte[] token) {
        String base64encodedToken = Base64.getEncoder().encodeToString(token);
        return "<wsse:BinarySecurityToken xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\""
                + " EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\""
                + " ValueType=\"" + OIDC_TOKEN_TYPE + "\" >" + base64encodedToken + "</wsse:BinarySecurityToken>";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [tokenHandler=" + tokenHandler + "]";
    }
}

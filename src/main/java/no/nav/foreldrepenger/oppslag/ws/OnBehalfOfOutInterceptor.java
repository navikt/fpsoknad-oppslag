package no.nav.foreldrepenger.oppslag.ws;

import static javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING;
import static org.apache.cxf.phase.Phase.SETUP;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;

@Component
@Scope(SCOPE_PROTOTYPE)
public class OnBehalfOfOutInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final String OIDC_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:jwt";
    private final TokenUtil tokenUtil;
    private static final Logger LOG = LoggerFactory.getLogger(OnBehalfOfOutInterceptor.class);

    public OnBehalfOfOutInterceptor(TokenUtil tokenUtil) {
        super(SETUP);
        this.tokenUtil = tokenUtil;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        if (tokenUtil.isExpired()) {
            LOG.warn("Token looks expired {}, should probably throw", tokenUtil.getExpiryDate());
            // throw new TokenExpiredException(tokenUtil.getExpiryDate(), null);
        }
        message.put(STS_TOKEN_ON_BEHALF_OF, createOnBehalfOfElement(tokenUtil.getToken()));
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
        return getClass().getSimpleName() + " [tokenHandler=" + tokenUtil + "]";
    }
}

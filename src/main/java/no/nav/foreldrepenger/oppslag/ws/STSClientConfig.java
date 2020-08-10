package no.nav.foreldrepenger.oppslag.ws;

import static org.apache.cxf.rt.security.SecurityConstants.PASSWORD;
import static org.apache.cxf.rt.security.SecurityConstants.USERNAME;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.security.trust.STSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class STSClientConfig {
    private static final String POLICY_PATH = "classpath:policy/";
    private static final String STS_CLIENT_AUTHENTICATION_POLICY = POLICY_PATH + "untPolicy.xml";
    @Value("${securitytokenservice.url}")
    private URI stsUrl;
    @Value("${fpselvbetjening.username:srvfpsoknad-oppslag}")
    private String serviceUser;
    @Value("${fpselvbetjening.password}")
    private String servicePwd;

    @Bean
    @Lazy
    public STSClient configureSTSClient(Bus bus) {
        STSClient stsClient = new STSClient(bus);
        stsClient.setEnableAppliesTo(false);
        stsClient.setAllowRenewing(false);
        stsClient.setLocation(stsUrl.toString());
        Map<String, Object> properties = new HashMap<>();
        properties.put(USERNAME, serviceUser);
        properties.put(PASSWORD, servicePwd);
        stsClient.setProperties(properties);
        // used for the STS client to authenticate itself to the STS provider.
        stsClient.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY);
        return stsClient;
    }
}

package no.nav.foreldrepenger.oppslag.ws;

import static org.apache.cxf.rt.security.SecurityConstants.PASSWORD;
import static org.apache.cxf.rt.security.SecurityConstants.USERNAME;

import java.net.URI;
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
    @Value("${securitytokenservice.username}")
    private String serviceUser;
    @Value("${securitytokenservice.password}")
    private String servicePwd;

    @Bean
    @Lazy
    public STSClient configureSTSClient(Bus bus) {
        STSClient sts = new STSClient(bus);
        sts.setEnableAppliesTo(false);
        sts.setAllowRenewing(false);
        sts.setLocation(stsUrl.toString());
        sts.setProperties(Map.of(USERNAME, serviceUser, PASSWORD, servicePwd));
        // used for the STS client to authenticate itself to the STS provider.
        sts.setPolicy(STS_CLIENT_AUTHENTICATION_POLICY);
        return sts;
    }
}

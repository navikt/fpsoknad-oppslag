package no.nav.foreldrepenger.lookup.ws;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.isDevOrPreprod;
import static org.apache.cxf.rt.security.SecurityConstants.PASSWORD;
import static org.apache.cxf.rt.security.SecurityConstants.USERNAME;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.ws.security.trust.STSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
class STSClientConfig implements EnvironmentAware {

    private static final String POLICY_PATH = "classpath:policy/";
    private static final String STS_CLIENT_AUTHENTICATION_POLICY = POLICY_PATH + "untPolicy.xml";

    private Environment env;
    @Value("${SECURITYTOKENSERVICE_URL}")
    private URI stsUrl;

    @Value("${FPSELVBETJENING_USERNAME}")
    private String serviceUser;

    @Value("${FPSELVBETJENING_PASSWORD}")
    private String servicePwd;

    @Bean
    public STSClient configureSTSClient(Bus bus) {
        STSClient stsClient = new STSClient(bus);
        if (isDevOrPreprod(env)) {
            stsClient.setFeatures(devAndPreprodFeatures());
        }
        else {
            stsClient.getInFaultInterceptors().add(new LoggingInInterceptor());
            stsClient.getOutFaultInterceptors().add(new LoggingOutInterceptor());
        }

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

    private static List<? extends Feature> devAndPreprodFeatures() {
        return newArrayList(loggingFeature());
    }

    private static Feature loggingFeature() {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        return loggingFeature;
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}

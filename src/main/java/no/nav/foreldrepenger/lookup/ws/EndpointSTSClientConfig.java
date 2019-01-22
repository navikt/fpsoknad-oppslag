package no.nav.foreldrepenger.lookup.ws;

import static org.apache.cxf.rt.security.SecurityConstants.CACHE_ISSUED_TOKEN_IN_ENDPOINT;
import static org.apache.cxf.rt.security.SecurityConstants.STS_CLIENT;

import java.util.Collections;
import java.util.List;

import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.policy.EndpointPolicy;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.attachment.reference.RemoteReferenceResolver;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.neethi.Policy;
import org.assertj.core.util.Lists;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.util.EnvUtil;

@Component
public class EndpointSTSClientConfig implements EnvironmentAware {

    private static final String POLICY_PATH = "classpath:policy/";
    private static final String STS_REQUEST_SAML_POLICY = POLICY_PATH + "requestSamlPolicy.xml";

    private STSClient stsClient;
    private Environment env;

    public EndpointSTSClientConfig(STSClient stsClient) {
        this.stsClient = stsClient;
        stsClient.setFeatures(features());
    }

    private List<? extends Feature> features() {
        if (EnvUtil.isDevOrPreprod(env)) {
            return Lists.newArrayList(loggingFeature());
        }
        return Collections.emptyList();
    }

    private Feature loggingFeature() {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        return loggingFeature;
    }

    public <T> T configureRequestSamlToken(T port) {
        Client client = ClientProxy.getClient(port);
        // do not have onbehalfof token so cache token in endpoint
        configureEndpointWithPolicyForSTS(stsClient, client, STS_REQUEST_SAML_POLICY, true);
        return port;
    }

    public <T> T configureRequestSamlTokenOnBehalfOfOidc(T port, OnBehalfOfOutInterceptor onBehalfOfOutInterceptor) {
        Client client = ClientProxy.getClient(port);
        client.getOutInterceptors().add(onBehalfOfOutInterceptor);

        // want to cache the token with the OnBehalfOfToken, not per proxy
        configureEndpointWithPolicyForSTS(stsClient, client, STS_REQUEST_SAML_POLICY, false);
        return port;
    }

    private void configureEndpointWithPolicyForSTS(STSClient stsClient, Client client, String policyReference,
            boolean cacheTokenInEndpoint) {
        client.getRequestContext().put(STS_CLIENT, stsClient);
        client.getRequestContext().put(CACHE_ISSUED_TOKEN_IN_ENDPOINT, cacheTokenInEndpoint);
        setEndpointPolicyReference(client, policyReference);
    }

    private void setEndpointPolicyReference(Client client, String uri) {
        Policy policy = resolvePolicyReference(client, uri);
        setClientEndpointPolicy(client, policy);
    }

    private Policy resolvePolicyReference(Client client, String uri) {
        PolicyBuilder policyBuilder = client.getBus().getExtension(PolicyBuilder.class);
        return new RemoteReferenceResolver("", policyBuilder).resolveReference(uri);
    }

    private void setClientEndpointPolicy(Client client, Policy policy) {
        Endpoint endpoint = client.getEndpoint();
        EndpointInfo endpointInfo = endpoint.getEndpointInfo();

        PolicyEngine policyEngine = client.getBus().getExtension(PolicyEngine.class);
        SoapMessage message = new SoapMessage(Soap12.getInstance());
        EndpointPolicy endpointPolicy = policyEngine.getClientEndpointPolicy(endpointInfo, null, message);
        policyEngine.setClientEndpointPolicy(endpointInfo, endpointPolicy.updatePolicy(policy, message));
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;

    }
}

package no.nav.foreldrepenger.lookup.ws;

import static com.google.common.collect.Lists.newArrayList;
import static no.nav.foreldrepenger.lookup.util.EnvUtil.isDevOrPreprod;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class WsClient<T> implements EnvironmentAware {

    @Inject
    private EndpointSTSClientConfig endpointStsClientConfig;

    @Inject
    private OnBehalfOfOutInterceptor onBehalfOfOutInterceptor;

    private Environment env;

    public T createPortForExternalUser(String serviceUrl, Class<?> portType) {
        T port = createAndConfigurePort(serviceUrl, portType);
        endpointStsClientConfig.configureRequestSamlTokenOnBehalfOfOidc(port, onBehalfOfOutInterceptor);
        return port;
    }

    public T createPortForSystemUser(String serviceUrl, Class<?> portType) {
        T port = createAndConfigurePort(serviceUrl, portType);
        endpointStsClientConfig.configureRequestSamlToken(port);
        return port;
    }

    @SuppressWarnings("unchecked")
    private T createAndConfigurePort(String serviceUrl, Class<?> portType) {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(portType);
        jaxWsProxyFactoryBean.setAddress(Objects.requireNonNull(serviceUrl));
        T port = (T) jaxWsProxyFactoryBean.create();
        Client client = ClientProxy.getClient(port);
        jaxWsProxyFactoryBean.getOutInterceptors().add(new CallIdHeaderInterceptor());
        if (isDevOrPreprod(env)) {
            jaxWsProxyFactoryBean.setFeatures(devAndPreprodFeatures());
        }
        else {
            client.getInFaultInterceptors().add(new LoggingInInterceptor());
            client.getOutFaultInterceptors().add(new LoggingOutInterceptor());
        }

        client.getOutInterceptors().add(new CallIdHeaderInterceptor());
        return port;
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

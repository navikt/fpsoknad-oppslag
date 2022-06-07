package no.nav.foreldrepenger.oppslag.ws;

import static no.nav.boot.conditionals.EnvUtil.isDevOrLocal;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
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
        var jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(portType);
        jaxWsProxyFactoryBean.setAddress(Objects.requireNonNull(serviceUrl));
        T port = (T) jaxWsProxyFactoryBean.create();
        Client client = ClientProxy.getClient(port);

        if (isDevOrLocal(env)) {
            client.getInFaultInterceptors().add(new LoggingInInterceptor());
            client.getOutFaultInterceptors().add(new LoggingOutInterceptor());
        }
        client.getOutInterceptors().add(new CallIdHeaderInterceptor());
        return port;
    }

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}

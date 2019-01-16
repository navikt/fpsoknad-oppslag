package no.nav.foreldrepenger.lookup.ws;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.stereotype.Component;

import no.nav.foreldrepenger.lookup.util.TokenUtil;

@Component
public class WsClient<T> {

    @Inject
    private EndpointSTSClientConfig endpointStsClientConfig;

    @Inject
    private OnBehalfOfOutInterceptor onBehalfOfOutInterceptor;

    @Inject
    protected TokenUtil tokenHandler;

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
        client.getOutInterceptors().add(new CallIdHeader());
        return port;
    }
}

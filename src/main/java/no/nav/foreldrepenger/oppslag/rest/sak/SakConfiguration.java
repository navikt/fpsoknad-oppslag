package no.nav.foreldrepenger.oppslag.rest.sak;

import static java.util.stream.Collectors.toCollection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.security.token.support.spring.validation.interceptor.BearerTokenClientHttpRequestInterceptor;

@Configuration
public class SakConfiguration {

    @Value("${SAK_SAKER_URL}")
    private URI sakBaseUrl;

    @Value("${SECURITYTOKENSERVICE_URL}")
    private URI stsUrl;

    @Value("${FPSELVBETJENING_USERNAME:srvfpsoknad-oppslag}")
    private String serviceUser;

    @Value("${FPSELVBETJENING_PASSWORD}")
    private String servicePwd;

    @Bean
    @Lazy
    public RestOperations restOperationsSak(ClientHttpRequestInterceptor... interceptors) {
        List<ClientHttpRequestInterceptor> interceptorListWithoutAuth = Arrays.stream(interceptors)
                // We'll add our own auth header with SAML elsewhere
                .filter(i -> !(i instanceof BearerTokenClientHttpRequestInterceptor))
                .collect(toCollection(ArrayList::new));

        ClientHttpRequestInterceptor[] interceptorsAsArray = interceptorListWithoutAuth.stream()
                .toArray(ClientHttpRequestInterceptor[]::new);

        return new RestTemplateBuilder()
                .interceptors(interceptorsAsArray)
                .build();
    }

    @Bean
    public StsClient stsClient(RestOperations restOperations) {
        return new StsClientHttp(restOperations, stsUrl, serviceUser, servicePwd);
    }

    @Bean
    public SakClient sakClient(RestOperations restOperations, StsClient stsClient, TokenUtil tokenUtil) {
        return new SakClientHttp(sakBaseUrl, restOperations, stsClient, tokenUtil);
    }
}

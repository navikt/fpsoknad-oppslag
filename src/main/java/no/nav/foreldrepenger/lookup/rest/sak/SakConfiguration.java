package no.nav.foreldrepenger.lookup.rest.sak;

import static java.util.stream.Collectors.toCollection;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestOperations;

import io.github.resilience4j.retry.Retry;
import no.nav.foreldrepenger.lookup.util.RetryUtil;
import no.nav.foreldrepenger.lookup.util.TokenUtil;
import no.nav.security.spring.oidc.validation.interceptor.BearerTokenClientHttpRequestInterceptor;

@Configuration
public class SakConfiguration {

    private static final String SAK_RETRY_CONFIG = "sakRetryConfig";
    private static final String STS_RETRY_CONFIG = "stsRetryConfig";

    @Value("${SAK_SAKER_URL}")
    private URI sakBaseUrl;

    @Value("${SECURITYTOKENSERVICE_URL}")
    private URI stsUrl;

    @Value("${FPSELVBETJENING_USERNAME}")
    private String serviceUser;

    @Value("${FPSELVBETJENING_PASSWORD}")
    private String servicePwd;

    @Bean
    public RestOperations restOperationsSak(TokenUtil tokenHandler, ClientHttpRequestInterceptor... interceptors) {
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
    public StsClient stsClient(RestOperations restOperations, @Qualifier(STS_RETRY_CONFIG) Retry retry) {
        return new StsClient(restOperations, stsUrl, serviceUser, servicePwd, retry);
    }

    @Bean
    public SakClientHttp sakClient(RestOperations restOperations, StsClient stsClient, TokenUtil tokenUtil,
            @Qualifier(SAK_RETRY_CONFIG) Retry retry) {
        return new SakClientHttp(sakBaseUrl, restOperations, stsClient, tokenUtil, retry);
    }

    @Bean
    @Qualifier(SAK_RETRY_CONFIG)
    public Retry sakRetry(@Value("${retry.sak.max:2}") int max) {
        return RetryUtil.retry(max, "saker", HttpServerErrorException.class, getLogger(SakClientHttp.class));
    }

    @Bean
    @Qualifier(STS_RETRY_CONFIG)
    public Retry stsRetry(@Value("${retry.sts.max:2}") int max) {
        return RetryUtil.retry(max, "STS", HttpServerErrorException.class, getLogger(StsClient.class));
    }
}

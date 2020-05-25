package no.nav.foreldrepenger.oppslag.rest.filters;

import static java.util.stream.Collectors.toCollection;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestOperations;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;

@Configuration
public class STSConfiguration {

    @Bean
    @Qualifier("STS")
    public RestOperations restOperationsSak(TokenUtil tokenHandler, ClientHttpRequestInterceptor... interceptors) {
        return new RestTemplateBuilder()
                .interceptors(Arrays.stream(interceptors)
                        .collect(toCollection(ArrayList::new)))
                .build();
    }
}

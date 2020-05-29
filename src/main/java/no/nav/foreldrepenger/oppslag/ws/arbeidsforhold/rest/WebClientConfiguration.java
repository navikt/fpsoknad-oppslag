package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.rest;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;

@Configuration
public class WebClientConfiguration {

    @Value("${SECURITYTOKENSERVICE_URL}")
    private URI stsUrl;

    @Value("${FPSELVBETJENING_USERNAME}")
    private String serviceUser;

    @Value("${FPSELVBETJENING_PASSWORD}")
    private String servicePwd;

    @Bean
    public WebClient webClient(TokenUtil tokenHandler, WebClient.Builder builder, @Value("${sts.uri}") String url) {
        return builder
                .baseUrl(url)
                .defaultHeaders(header -> header.setBasicAuth(serviceUser, servicePwd))
                .build();
    }
}

package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Value("${FPSELVBETJENING_USERNAME}")
    private String serviceUser;

    @Value("${FPSELVBETJENING_PASSWORD}")
    private String servicePwd;

    @Bean
    public WebClient webClient(WebClient.Builder builder, @Value("${sts.uri}") String url) {
        return builder
                .baseUrl(url)
                .defaultHeaders(header -> header.setBasicAuth(serviceUser, servicePwd))
                .build();
    }
}

package no.nav.foreldrepenger.oppslag.inntekt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.inntekt.v3.binding.InntektV3;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InntektConfiguration extends WsClient<InntektV3>{

    @SuppressWarnings("unchecked")
    @Bean
    public InntektV3 inntektV3(@Value("${VIRKSOMHET_INNTEKT_V3_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, InntektV3.class);
    }

    @Bean
    public InntektClient inntektClientWs(InntektV3 inntektV3) {
        return new InntektClientWs(inntektV3);
    }


}

package no.nav.foreldrepenger.oppslag.fpsak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.foreldrepengesak.v1.binding.ForeldrepengesakV1;

@Configuration
public class FpsakConfiguration extends WsClient<ForeldrepengesakV1>{

    @SuppressWarnings("unchecked")
    @Bean
    public ForeldrepengesakV1 fpsakV1(@Value("${VIRKSOMHET_FORELDREPENGESAK_V1_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, ForeldrepengesakV1.class);
    }

    @Bean
    public FpsakClient fpsakClientWs(ForeldrepengesakV1 fpsakV1) {
        return new FpsakClientWs(fpsakV1);
    }
}

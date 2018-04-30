package no.nav.foreldrepenger.oppslag.infotrygd;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.infotrygdsak.v1.binding.InfotrygdSakV1;

@Configuration
public class InfotrygdConfiguration extends WsClient<InfotrygdSakV1>{

    @SuppressWarnings("unchecked")
    @Bean
    public InfotrygdSakV1 infotrygdSakV1(@Value("${VIRKSOMHET_INFOTRYGDSAK_V1_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, InfotrygdSakV1.class);
    }

    @Bean
    public InfotrygdClient infotrygdClientWs(InfotrygdSakV1 infotrygdSakV1) {
        return new InfotrygdClientWs(infotrygdSakV1);
    }

}

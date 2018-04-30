package no.nav.foreldrepenger.oppslag.arena;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.binding.YtelseskontraktV3;

@SpringBootConfiguration
@ComponentScan(basePackages = { "no.nav.foreldrepenger.oppslag" })
public class ArenaConfiguration extends WsClient<YtelseskontraktV3>{

    @SuppressWarnings("unchecked")
    @Bean
    public YtelseskontraktV3 YtelseskontraktV3(
            @Value("${VIRKSOMHET_YTELSESKONTRAKT_V3_ENDPOINTURL}") String serviceUrl) {
        return createPort(serviceUrl, YtelseskontraktV3.class);
    }

    @Bean
    public ArenaClient arenaClientWs(YtelseskontraktV3 ytelser) {
        return new ArenaClientWs(ytelser);
    }
}

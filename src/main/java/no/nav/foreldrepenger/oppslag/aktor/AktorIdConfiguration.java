package no.nav.foreldrepenger.oppslag.aktor;

import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AktorIdConfiguration extends WsClient<AktoerV2>{

   @SuppressWarnings("unchecked")
   @Bean
   public AktoerV2 aktorV2(@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
      return createPort(serviceUrl, AktoerV2.class);
   }


    @Bean
    public AktorIdClient aktorIdClientWs(AktoerV2 aktør) {
        return new AktorIdClientWs(aktør);
    }
}

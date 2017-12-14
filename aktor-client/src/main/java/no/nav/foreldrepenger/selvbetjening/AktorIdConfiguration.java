package no.nav.foreldrepenger.selvbetjening;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.selvbetjening.cxfclient.CXFClient;
import no.nav.foreldrepenger.selvbetjening.cxfclient.STSConfig;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;

@Configuration
public class AktorIdConfiguration {
	
@Bean
public AktoerV2 aktorV2 (STSConfig stsConfig,@Value("${AKTOER_V2_ENDPOINTURL}") String serviceUrl) {
      return new CXFClient<>(AktoerV2.class)
         .configureStsForSystemUser(stsConfig)
         .serviceUrl(serviceUrl)
         .build();
   }
}   



package no.nav.foreldrepenger.selvbetjening.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.selvbetjening.cxfclient.STSConfig;

@Configuration
public class SecurityConfiguration {

  
@Bean
public STSConfig stsConfig(@Value("${SECURITYTOKENSERVICE_URL}") String stsUrl,
	                       @Value("${FPSELVBETJENING_USERNAME}") String userName,
	                       @Value("${FPSELVBETJENING_PASSWORD}") String password) {
		return new STSConfig(stsUrl,userName,password);
	}

}

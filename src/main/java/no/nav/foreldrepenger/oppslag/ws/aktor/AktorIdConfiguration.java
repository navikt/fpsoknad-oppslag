package no.nav.foreldrepenger.oppslag.ws.aktor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.oppslag.util.TokenUtil;
import no.nav.foreldrepenger.oppslag.ws.WsClient;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.spring.SpringTokenValidationContextHolder;
import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;

@Configuration
public class AktorIdConfiguration extends WsClient<AktoerV2> {

    private static final String HEALTH_INDICATOR_AKTØR = "healthIndicatorAktør";
    public static final String AKTOER_V2 = "aktoerV2";

    @ConditionalOnMissingBean(SpringTokenValidationContextHolder.class)
    TokenValidationContextHolder dummyContextHolderForDev() {
        return new TokenValidationContextHolder() {

            @Override
            public TokenValidationContext getTokenValidationContext() {
                return null;
            }

            @Override
            public void setTokenValidationContext(TokenValidationContext tokenValidationContext) {
            }

        };
    }

    @Bean
    @Qualifier(AKTOER_V2)
    public AktoerV2 aktoerV2(@Value("${aktoer.v2.endpointurl}") String serviceUrl) {
        return createPortForExternalUser(serviceUrl, AktoerV2.class);
    }

    @Bean
    @Qualifier(HEALTH_INDICATOR_AKTØR)
    public AktoerV2 healthIndicatorAktør(@Value("${aktoer.v2.endpointurl}") String serviceUrl) {
        return createPortForSystemUser(serviceUrl, AktoerV2.class);
    }

    @Bean
    public AktørTjeneste aktorIdClientWs(@Qualifier(AKTOER_V2) AktoerV2 aktoerV2,
            @Qualifier(HEALTH_INDICATOR_AKTØR) AktoerV2 healthIndicator, TokenUtil tokenHandler) {
        return new AktørIdClientWs(aktoerV2, healthIndicator, tokenHandler);
    }

}

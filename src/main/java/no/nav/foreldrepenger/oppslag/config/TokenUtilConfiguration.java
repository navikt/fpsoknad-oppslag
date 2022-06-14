package no.nav.foreldrepenger.oppslag.config;

import static no.nav.foreldrepenger.common.util.TokenUtil.TOKENX;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.nav.foreldrepenger.common.util.TokenUtil;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;

@Configuration
public class TokenUtilConfiguration {

    @Bean
    public TokenUtil tokenUtil(TokenValidationContextHolder contextHolder) {
        return new TokenUtil(contextHolder, TOKENX);
    }
}

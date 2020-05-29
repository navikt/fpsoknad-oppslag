package no.nav.foreldrepenger.oppslag.ws.arbeidsforhold.rest;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserToken {

    private final String accessToken;

    private final Long expiresIn;

    private final String tokenType;

    private final LocalDateTime issuedTime = LocalDateTime.now();

    @JsonCreator
    public UserToken(@JsonProperty("access_token") String accessToken, @JsonProperty("expires_in") Long expiresIn,
            @JsonProperty("token_type") String tokenType) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
    }

    /**
     *
     * @param expirationLeeway the amount of seconds to be subtracted from the
     *                         expirationTime to avoid returning false positives
     * @return <code>true</code> if "now" is after the expirationtime(minus leeway),
     *         else returns <code>false</code>
     */
    public boolean isExpired(long expirationLeeway) {
        return LocalDateTime.now().isAfter(issuedTime.plusSeconds(expiresIn).minusSeconds(expirationLeeway));
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}
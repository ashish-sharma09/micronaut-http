package micronaut.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {

    private final String accessToken;
    private final long expiresAt;

    @JsonCreator
    public Token(
            @JsonProperty("token") final String accessToken,
            @JsonProperty("expires_in") long tokenExpiresInSec
    ) {
        this.accessToken = accessToken;
        this.expiresAt = System.currentTimeMillis() + (tokenExpiresInSec * 1000);
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public boolean isTokenExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}

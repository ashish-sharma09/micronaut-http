package micronaut.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthRequest {
    @JsonProperty("client_id")
    private final String clientId;

    @JsonCreator
    public AuthRequest(final String clientId) {
        this.clientId = clientId;
    }
}


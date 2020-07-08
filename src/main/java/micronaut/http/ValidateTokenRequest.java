package micronaut.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidateTokenRequest {

    @JsonProperty("token")
    public final String token;

    public ValidateTokenRequest(String token) {
        this.token = token;
    }
}

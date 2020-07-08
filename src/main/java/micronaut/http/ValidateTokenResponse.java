package micronaut.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.DefaultAuthentication;

import java.util.Collections;

public class ValidateTokenResponse {
    private final String status;

    @JsonCreator
    ValidateTokenResponse(
            @JsonProperty(value = "Status", required = true) String status
    ) {
        this.status = status;
    }

    boolean isTokenValid() {
        return status.equals("VALID");
    }

    Authentication asAuthentication() {
        return new DefaultAuthentication(status, Collections.emptyMap());
    }
}

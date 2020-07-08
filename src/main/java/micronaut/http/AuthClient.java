package micronaut.http;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;

@Client("${auth.url}")
public interface AuthClient {

    @Post("/auth/validate?client_id={clientId}")
    Flowable<ValidateTokenResponse> validateToken(
            @Body ValidateTokenRequest validateTokenRequest,
            @QueryValue(value="clientId") String clientId
    );

    @Post(value = "/issue/token", consumes = "application/json")
    Token retrieveToken(@Body AuthRequest issueTokenRequest);
}

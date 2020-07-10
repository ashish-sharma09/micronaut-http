package micronaut.http;

import io.micronaut.context.annotation.Property;
import io.reactivex.Single;

import javax.inject.Singleton;

@Singleton
public class TokenIssuer {

    private final AuthClient authClient;
    private final String clientId;

    public TokenIssuer(
            AuthClient authClient,
            @Property(name = "client.id") String clientId
    ) {
        this.authClient = authClient;
        this.clientId = clientId;
    }

    public Single<Token> retrieveToken() {
        return authClient.retrieveToken(new AuthRequest(clientId));
    }
}

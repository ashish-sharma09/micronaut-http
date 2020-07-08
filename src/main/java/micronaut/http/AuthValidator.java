package micronaut.http;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.core.order.Ordered;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.validator.TokenValidator;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthValidator implements TokenValidator {

    private AuthClient authClient;

    private String clientId;

    @Inject
    public AuthValidator(AuthClient authClient,
                         @Value("${client.id}") String clientId) {
        this.authClient = authClient;
        this.clientId = clientId;
    }

    @Override
    @SingleResult
    public Publisher<Authentication> validateToken(String token) {
        return authClient
                .validateToken(new ValidateTokenRequest(token), clientId)
                .filter(ValidateTokenResponse::isTokenValid)
                .map(ValidateTokenResponse::asAuthentication)
                .doOnError(this::handle);
    }

    private void handle(Throwable throwable) {
        throw new RuntimeException(throwable);
    }

    //highest precedence causes this validator to be used before others, reducing number of artificial errors in the logs
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

package micronaut.http;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import org.reactivestreams.Publisher;

import javax.inject.Inject;

@Filter("/**/details/*")
public class AuthenticateFilter implements HttpClientFilter {

    private final TokenIssuer tokenIssuer;

    @Inject
    public AuthenticateFilter(TokenIssuer tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
        return chain.proceed(request.bearerAuth(tokenIssuer.retrieveToken().getAccessToken()));
    }
}

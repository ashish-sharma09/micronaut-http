package micronaut.http;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
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
        return Flowable.fromCallable(tokenIssuer::retrieveToken)
            .subscribeOn(Schedulers.io())
            .flatMap(res -> chain.proceed(request.bearerAuth(res.getAccessToken())));
    }
}

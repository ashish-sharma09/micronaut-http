package micronaut.http;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpStatus;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Requires(property = "service.url")
public class RequestValidator {

    private final ServiceClient serviceClient;

    @Inject
    public RequestValidator(ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    public boolean isValid(final String id) {
        return serviceClient.getDetails(id).getStatus().equals(HttpStatus.OK);
    }

}
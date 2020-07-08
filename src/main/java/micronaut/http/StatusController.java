package micronaut.http;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import javax.inject.Inject;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
public class StatusController {
    private final RequestValidator requestValidator;

    @Inject
    public StatusController(RequestValidator requestValidator) {
        this.requestValidator = requestValidator;
    }

    @Get("/status")
    String status() {
        return requestValidator.isValid("requestId") ? "valid" : "invalid";
    }
}

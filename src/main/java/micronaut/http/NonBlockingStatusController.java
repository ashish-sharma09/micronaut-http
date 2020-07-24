package micronaut.http;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class NonBlockingStatusController {

    private Logger logger = LoggerFactory.getLogger(NonBlockingStatusController.class);

    @Get("/_status")
    Single<String> status() {
        logger.info("Non blocking status");
        return Single.just("OK" + Thread.currentThread().getName());
    }
}

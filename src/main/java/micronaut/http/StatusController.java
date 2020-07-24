package micronaut.http;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Controller
public class StatusController {

    private Logger logger = LoggerFactory.getLogger(NonBlockingStatusController.class);

    @Get("/status")
    String status() throws InterruptedException {
//        logger.info("Blocking status thread: " + Thread.currentThread().getName());
        Thread.sleep(200);
        return "OK -> " + Thread.currentThread().getName();
    }
}

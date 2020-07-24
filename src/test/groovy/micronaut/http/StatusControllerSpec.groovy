package micronaut.http

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import io.restassured.RestAssured
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class StatusControllerSpec extends Specification {

    def context

    void setup() {
        context = ApplicationContext
                .build()
                .mainClass(EmbeddedServer)
                .build()
        context.start()

        def server = context.getBean(EmbeddedServer)
        server.start()

        RestAssured.port = server.port
    }

    void cleanup() {
        context.stop()
    }

    def "Non blocking controller should not be too affected with requests to a blocking controller"() {
        given:
        ExecutorService threadPool = Executors.newFixedThreadPool(50)

        when: "application is warmed up"
        get("/status", threadPool, 5).every{it.get() > 0.0}
        get("/_status", threadPool, 5).every{it.get() > 0.0}

        and: "Multiple concurrent requests are made to BlockingController"
        get("/status", threadPool, 20)

        and: "At the same time requests are made to non-blocking controller"
        List<Future<Long>> nonBlockingFutures = get("/_status", threadPool, 20)

        then: "non blocking futures average response time should not be slower than 10ms"
        nonBlockingFutures.stream().mapToLong{it.get()}.average().getAsDouble() <= 100.0
    }

    def "Non blocking controller should be fast when there are no requests to a blocking controller"() {
        given:
        ExecutorService threadPool = Executors.newFixedThreadPool(20)

        when: "application is warmed up"
        get("/status", threadPool, 5).every{it.get() > 0.0}
        get("/_status", threadPool, 5).every{it.get() > 0.0}

        and: "requests are made"
        List<Future<Long>> nonBlockingFutures = get("/_status", threadPool, 10)

        then: "non blocking futures average response time should not be slower than 10ms"
        nonBlockingFutures.stream().mapToLong{it.get()}.peek{println(it)}.average().getAsDouble() <= 100.0
    }

    private List<Future<Long>> get(String endpoint, ExecutorService threadPool, Integer concurrency) {
        List<Future<Long>> futures = []

        concurrency.times {
            futures.add(threadPool.submit(new Callable<Long>() {
                @Override
                Long call() throws Exception {
                    return RestAssured
                        .given()
                        .header("Content-Type", "application/json")
                        .get(endpoint)
                        .time()
                }
            }))
        }
        futures
    }
}
